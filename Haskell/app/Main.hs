{-# LANGUAGE OverloadedStrings #-}
{-# LANGUAGE DeriveGeneric #-}
module Main where


import qualified MyLib (someFunc)
import qualified Parser
import Web.Scotty
import Data.Monoid (mconcat)
import Data.Aeson
import Data.Either
import Control.Monad.Trans(liftIO,lift,MonadIO)
import GHC.Generics
import System.IO  

data JsonMessage = JsonMessage{name::String, mbody::String}
instance Show JsonMessage where
    show (JsonMessage name mbody) = "The message body is: "++show mbody


instance FromJSON JsonMessage where
    parseJSON (Object v) = JsonMessage <$> v .: "name" <*> v .: "mbody"
    parseJSON _ = mempty

instance ToJSON JsonMessage where
    toJSON (JsonMessage name mbody) = object ["name" .= name, "mbody" .= mbody]

instance ToJSON Parser.MARC where
    toJSON marc  = object ["name" .= show ("PARSED MARC"), "mbody" .= show (marc ::Parser.MARC)]

instance ToJSON Parser.MARCLine where
    toJSON marc  = object ["name" .= show ("PARSED MARCLine"), "mbody" .= show (marc ::Parser.MARCLine)]

 -- TODO
 --Change type  
parse :: Maybe JsonMessage -> IO(Parser.MARC)
parse (Just (JsonMessage name body)) = do
        putStrLn $ show body
        dp <- Parser.directParse body
        -- putStrLn $ show dp
        case dp of
            Left err -> return ( Parser.MARC Nothing Nothing [])
            Right marc -> return (marc)

parseLine :: Maybe JsonMessage -> IO(Parser.MARCLine)
parseLine (Just (JsonMessage name body)) = do
        putStrLn $ show body
        dp <- Parser.directParseLine body
        -- putStrLn $ show dp
        case dp of
            Left err -> return ( Parser.MARCLine Nothing Nothing Nothing)
            Right marc -> return (marc)
        

main :: IO ()
main = scotty 3000 $ do

    get "/json" $ do
        b<-body
        i<-liftIO $ putStrLn $ show (decode b :: Maybe JsonMessage)
        Web.Scotty.json $ JsonMessage "Test" "Body"

    post "/parse/file" $ do
        liftIO $ putStrLn "request arrived"
        b<-body
        i<-liftIO $ parse $ (decode b :: Maybe JsonMessage)
        liftIO $ putStrLn $ show $ i
        Web.Scotty.json $ i

    post "/parse/line" $ do
        liftIO $ putStrLn "request arrived"
        b<-body
        i<-liftIO $ parseLine $ (decode b :: Maybe JsonMessage)
        liftIO $ putStrLn $ show $ i
        Web.Scotty.json $ i


-- main :: IO ()
-- main = do
--   putStrLn "Hello, Haskell!"
--   MyLib.someFunc
 


-- routes :: Connection -> IO ()
-- routes conn = scotty 8080 $ do
--   get "/api/product/" $ getProducts conn

--   get "/api/product/:id" $ getProduct conn

--   post "/api/product/" $ createProduct conn

--   put "/api/product/:id" $ updateProduct conn

--   delete "/api/product/:id" $ deleteProduct conn


    -- get "/:word" $ do
    --     beam <- param "word"
    --     html $ mconcat ["<h1>Scotty, ", beam, " me up!</h1>"]

    -- get "/test/me" $ do
    --     html $ mconcat ["test", " me up!"]