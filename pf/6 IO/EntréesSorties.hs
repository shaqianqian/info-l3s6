{-# LANGUAGE CPP #-}

module EntréesSorties where

import ParserCours (parseEval)

import Control.Applicative

affiche :: String -> ()
affiche = undefined

ex1 = [ affiche "abc", affiche "def" ]

id' :: a -> a
id' x = let _ = putStr "boum"
        in  x

-- echo = let c = getChar
--        in  putChar c
echo :: IO ()
echo = getChar >>= \c -> putChar c

echo2 :: IO ()
echo2 = getChar   >>= \c ->
        -- putChar c >>= \_ ->
        putChar c >>
        putChar c

echo2' :: IO ()
echo2' = do c <- getChar
            putChar c
            putChar c

-- Une fonction qui lit deux caractères et les « retourne »

deuxcars :: IO (Char, Char)
deuxcars = getChar >>= \c1 ->
           getChar >>= \c2 ->
           pure (c1, c2)
           -- return (c1, c2)

deuxcars' :: IO (Char, Char)
deuxcars' = do c1 <- getChar
               c2 <- getChar
               pure (c1, c2)

deuxcars'' :: IO String
deuxcars'' = getChar >>= \c1 ->
             getChar >>= \c2 ->
             pure [c1, c2]
           -- return (c1, c2)

-- Une fonction qui lit une ligne complète
litligne :: IO String
litligne = do c <- getChar
              if c == '\n'
                then pure "\n"
                else do cs <- litligne
                        pure (c:cs)

#if __GLASGOW_HASKELL__ >= 800

-- La définition de l’instance d’Alternative pour le type IO n’a été
-- défini qu’à partir de la version 4.9.0 de la bibliothèque base
-- (celle distribuée avec GHC 8.0)
-- La définition des fonctions suivantes seraient moins directement
-- similaires à celles données pour le type Parser

getCharButEOL :: IO Char
getCharButEOL = do c <- getChar
                   if c == '\n'
                       then empty
                       else pure c

getCharWhen :: (Char -> Bool) -> IO Char
getCharWhen cond = getChar >>= filterChar
    where filterChar c | cond c    = pure c
                       | otherwise = empty

getCharButEOL' :: IO Char
getCharButEOL' = getCharWhen (/= '\n')

getLine' :: IO String
getLine' = many getCharButEOL'

#else

-- Sans l’instance d’Alternative pour IO, on utilise donc directement
-- la fonction getLine standard

getLine' :: IO String
getLine' = getLine

#endif

interagitUneLigne :: IO ()
interagitUneLigne = do l <- getLine'
                       print (parseEval l)

repeteJusqua :: IO Bool -> IO () -> IO ()
repeteJusqua arret a = do arr <- arret
                          if not arr
                              then do a
                                      repeteJusqua arret a
                              else pure ()

eternellement :: IO () -> IO ()
eternellement a = do a
                     eternellement a

interprete :: IO ()
interprete = eternellement interagitUneLigne

main :: IO ()
main = putStr "boum"
