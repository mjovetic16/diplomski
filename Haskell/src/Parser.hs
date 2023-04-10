module Parser where

import Text.Parsec
import System.Environment   
import System.Directory  
import System.IO  
import Data.List  
import Data.Char
import Control.Monad.Trans(liftIO,lift,MonadIO)

data MARC = MARC (Maybe Leader) (Maybe [CtrlField]) [DataField]
data MARCLine = MARCLine (Maybe Leader) (Maybe CtrlField) (Maybe DataField)
data Leader = Leader String 
data CtrlField = CtrlField {controlTag :: String, controlData :: String}
data DataField = DataField {tag :: String, indikator :: Maybe String, subfields :: [(Char,String)]}

instance Show Leader where
    show (Leader s) = "\"leader\": "++show s

instance Show CtrlField where
    show (CtrlField controlTag controlData) = "{"++show controlTag ++":"++show controlData++"}"

instance Show DataField where
    show (DataField tag ind subfields) = "{"++show tag++":{\"subfields\":["++ showArray (array subfields)++"],\n"++showInd ind++"}}"
        where showArray [] =  ""
              showArray (x:[]) = x
              showArray (x:xs) = x ++",\n"++ showArray xs
              showInd Nothing = showIndHelp "  "
                where showIndHelp (x:[]) ="\"ind1\":"++ showInd1 x
                        where showInd1 '#' = show " "
                              showInd1 '_' = show " "
                              showInd1 a = "\""++[a]++"\""  
                      showIndHelp (x:y:[]) = "\"ind1\":"++showInd1 x++",\n\"ind2\":"++showInd1 y
                        where showInd1 '#' = show " "
                              showInd1 '_' = show " "
                              showInd1 a = "\""++[a]++"\""
              showInd (Just x) = showIndHelp x
                where showIndHelp (x:[]) ="\"ind1\":"++ showInd1 x
                        where showInd1 '#' = show " "
                              showInd1 '_' = show " "
                              showInd1 a = "\""++[a]++"\""  
                      showIndHelp (x:y:[]) = "\"ind1\":"++showInd1 x++",\n\"ind2\":"++showInd1 y
                        where showInd1 '#' = show " "
                              showInd1 '_' = show " "
                              showInd1 a = "\""++[a]++"\"" 
              array [] = [] 
              array (x:xs) = arrayHelp x : array xs
                where arrayHelp (c,dat) = "{\""++ [c] ++ "\":\"" ++ dat ++ "\"}"

instance Show MARC where
    show (MARC (Just l) (Just []) d) = "{"++show l++",\n\"fields\": ["++ showArray d++"]}"
        where showArray (x:[]) = show x
              showArray (x:xs) = show x++",\n" ++ showArray xs
              showArray [] = ""
    show (MARC Nothing (Just []) d) = "{\"fields\": ["++ showArray d++"]}"
        where showArray (x:[]) = show x
              showArray (x:xs) = show x++",\n" ++ showArray xs
              showArray [] = ""
    show (MARC (Just l) (Just c) d) = "{"++show l++",\n\"fields\": ["++ showArray c++",\n"++ showArray d++"]}"
        where showArray (x:[]) = show x
              showArray (x:xs) = show x++",\n" ++ showArray xs
              showArray [] = ""
    show (MARC Nothing (Just c) d) = "{\"fields\": ["++ showArray c++",\n"++ showArray d++"]}"
        where showArray (x:[]) = show x
              showArray (x:xs) = show x++",\n" ++ showArray xs
              showArray [] = ""
    show (MARC Nothing Nothing d) = "{Parse ERROR}"++ show d
    


instance Show MARCLine where
    show (MARCLine Nothing Nothing Nothing) = "Empty MARC"
    show (MARCLine (Just l) Nothing Nothing) = "Leader{"++show l++"}"
    show (MARCLine Nothing (Just c) Nothing) = "Control{"++show c++"}"
    show (MARCLine Nothing Nothing (Just d)) = "Data{"++show d++"}"
        


leader1 :: Parsec String () Leader
leader1 = do string "LDR"
             space
             txt<-count 24 anyChar
             endOfLine
             return $ Leader txt

leader2 :: Parsec String () Leader
leader2 = do string "000"
             txt<-count 24 anyChar
             endOfLine
             return $ Leader txt

leader3 :: Parsec String () Leader
leader3 = do string "LEADER"
             space
             txt<-count 24 anyChar
             endOfLine
             return $ Leader txt

ctrlField1 :: Parsec String () CtrlField
ctrlField1 = do ctrlTag <- count 3 digit
                space
                content <- manyTill anyChar endOfLine
                return $ CtrlField ctrlTag content

ctrlField2 :: Parsec String () CtrlField
ctrlField2 = do ctrlTag <- count 3 digit
                content <- manyTill anyChar endOfLine
                return $ CtrlField ctrlTag content

ctrlField3 :: Parsec String () CtrlField
ctrlField3 = do ctrlTag <- count 3 digit
                space
                content <- manyTill anyChar endOfLine
                return $ CtrlField ctrlTag content

dataField1 :: Parsec String () DataField
dataField1 = do tag <- count 3 digit
                space
                ind <- count 2 (oneOf $ ['0'..'9']++['#'])
                space
                subFields <- manyTill subField1 endOfLineOrFile
                return $ DataField tag (Just ind) subFields

subField1 :: Parsec String () (Char,String) 
subField1 = do char '$'
               fieldTag <- anyChar
               fieldData <- manyTill anyChar (lookAhead $ char '$'<|> lookAhead endOfLineOrFile)
               return (fieldTag,fieldData)       

dataField2 :: Parsec String () DataField
dataField2 = do tag <- count 3 digit
                ind <- count 2 (oneOf $ ['0'..'9']++['_'])
                space
                subFields <- manyTill subField2 endOfLineOrFile
                return $ DataField tag (Just ind) subFields

subField2 :: Parsec String () (Char,String) 
subField2 = do char '|'
               fieldTag <- anyChar
               space
               fieldData <- manyTill anyChar (try (space>>lookAhead  (char '|')) <|> lookAhead (endOfLineOrFile))
               return (fieldTag,fieldData)
-- TODO Space after indicator
dataField3 :: Parsec String () DataField
dataField3 = do tag <- count 3 digit
                space
                ind <- optionMaybe $ try $ manyTill (oneOf $ ['0'..'9']) space
                space
                subFields <- manyTill subField3 endOfLineOrFile
                return $ DataField tag ind subFields

subField3 :: Parsec String () (Char,String) 
subField3 = do char '$'
               fieldTag <- anyChar
               space
               fieldData <- manyTill anyChar (try (space>>lookAhead  (char '$')) <|> lookAhead (endOfLineOrFile))
               return (fieldTag,fieldData)

dataFieldUni1 :: Parsec String () DataField
dataFieldUni1 = do tag <- count 3 digit
                   space
                   ind <- count 2 (oneOf $ ['0'..'9']++['#'])
                   space
                   subFields <- manyTill subFieldUni1 endOfLineOrFile
                   return $ DataField tag (Just ind) subFields

subFieldUni1 :: Parsec String () (Char,String) 
subFieldUni1 = do char '['
                  fieldTag <- anyChar
                  char ']'
                  fieldData <- manyTill anyChar (lookAhead $ char '[' <|> lookAhead (endOfLineOrFile))
                  return (fieldTag,fieldData)

dataFieldUni2 :: Parsec String () DataField
dataFieldUni2 = do tag <- count 3 digit
                   ind <- count 2 (oneOf $ ['0'..'9']++[' '])
                   char (chr 31)
                   subFields <- manyTill subFieldUni2 (char (chr 30) <|> endOfLineOrFile)
                   return $ DataField tag (Just ind) subFields

subFieldUni2 :: Parsec String () (Char,String) 
subFieldUni2 = do fieldTag <- anyChar
                  fieldData <- manyTill anyChar ((char (chr 31)) <|> lookAhead (char (chr 30))<|>lookAhead endOfLineOrFile)
                  return (fieldTag,fieldData)

leader::Parsec String () Leader
leader = do ldr <- try leader1 <|> try leader2 <|> try leader3
            return ldr

ctrlField::Parsec String () CtrlField
ctrlField = do ctr <- try ctrlField1 <|> try ctrlField2 <|> try ctrlField3
               return ctr

dataField::Parsec String () DataField
dataField = do dt <- try dataField1 <|> try dataField2 <|> try dataField3 <|> try dataFieldUni1 <|> try dataFieldUni2
               return dt


parseMarc :: Parsec String () MARC
parseMarc = do leader <- optionMaybe (try leader)
               ctrlFields <- manyTill ctrlField (try $ lookAhead $ dataField)
               dataFields <- many $ try dataField
            
               return $ MARC leader (Just ctrlFields) dataFields

-- parseMarc :: Parsec String () MARC
-- parseMarc = do leader <- optionMaybe (try leader)
--                ctrlFields <- manyTill ctrlField (try $ lookAhead $ dataField)
--                dataFields <- many $ try dataField
            
--                return $ MARC leader (Just ctrlFields) dataFields

doParseMarc :: String -> Either ParseError MARC
doParseMarc s = parse parseMarc "" s

atom :: Parsec String () Char
atom = do return ' '

endOfLineOrFile :: Parsec String () Char
endOfLineOrFile = do endOfLine <|> (try $ (eof>>atom))

printJSON :: Either ParseError MARC -> MARC
printJSON (Right txt) = txt
printJSON (Left l) = MARC Nothing Nothing []

main :: IO(Either ParseError MARC)
main = do  args <- getArgs
           handle <- openFile (head args) ReadMode
           handleW <- openFile "jsonMarc.json" WriteMode    
           contents <- hGetContents handle  
           let txt = doParseMarc contents
           hPrint handleW (printJSON txt)
           hClose handle
           hClose handleW
           return txt  

mainParse :: String -> IO (Either ParseError MARC)
mainParse path = do handle <- openFile path ReadMode
                    handleW <- openFile "jsonMarc.json" WriteMode    
                    contents <- hGetContents handle  
                    putStrLn $ show $ contents
                    let txt = doParseMarc contents
                    hPrint handleW (printJSON txt)
                    hClose handle
                    hClose handleW
                    return $ txt

directParse :: String -> IO (Either ParseError MARC)
directParse contents = do 
                    putStrLn $ show $ contents
                    let txt = doParseMarc contents
                    return $ txt



parseMarcLine :: Parsec String () MARCLine
parseMarcLine = do 
    leader <- optionMaybe (try leader)
    dataFields <- optionMaybe (try dataField)
    ctrlFields <- optionMaybe (try ctrlField) 
    
    return $ MARCLine leader ctrlFields dataFields

doParseMarcLine :: String -> Either ParseError MARCLine
doParseMarcLine s = parse parseMarcLine "" s

directParseLine :: String -> IO (Either ParseError MARCLine)
directParseLine contents = do 
                    let txt = doParseMarcLine contents
                    return $ txt

