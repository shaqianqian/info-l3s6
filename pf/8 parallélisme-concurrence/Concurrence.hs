module Main where

-- Concurrence
-- - Objectif : écrire du code de façon modulaire
-- - Support : matériel parallèle (processeur
-- multi-cœur) ou pas (entrelacement)

import Control.Monad
import Control.Concurrent

main :: IO ()
main = main3

-- * 1er exemple : création d’un nouveau fil d’exécution

echo :: Int -> String -> IO ()
echo delais ident = forever $ do
    putStrLn (ident ++ " s’exécute")
    threadDelay delais

main1 = do forkIO (echo 700000 "fork")
           echo 200000 "main"

-- * 2e exemple : synchronisation de deux fils par
-- échange de message

echo2 :: MVar () -> Int -> Int -> String -> IO ()
echo2 fin delais repetitions ident =
    do replicateM_ repetitions $ do
            putStrLn (ident ++ " s’exécute")
            threadDelay delais
       putMVar fin ()

main2 :: IO ()
main2 = do fin <- newEmptyMVar
           forkIO (echo2 fin 700000 4 "fork")
           takeMVar fin
           -- pure ()

-- * Risque d’interblocage

renvoie :: MVar a -> MVar a -> IO ()
renvoie a b = do c <- takeMVar a
                 putMVar b c

main3 = do a <- newEmptyMVar
           b <- newEmptyMVar

           forkIO (renvoie a b)
           renvoie b a
