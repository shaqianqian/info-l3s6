module Extra where

import Parser

-- Réflexion autour d’Applicative pour les listes

-- Il y a plusieurs fonctions naturelles de type
-- [a -> b] -> [a] -> [b]

parallele :: [a -> b] -> [a] -> [b]
parallele (f:fs) (x:xs) = f x : parallele fs xs
parallele _      _      = []

toutesTous :: [a -> b] -> [a] -> [b]
toutesTous []     _  = []
toutesTous (f:fs) xs = map f xs ++ toutesTous fs xs

ex1 = parallele [id, (+1), (*2)] [1,2,3]
ex2 = toutesTous [id, (+1), (*2)] [1,2,3]
ex3 = [id, (+1), (*2)] <*> [1,2,3]


-- Petits exemples sur les Monad

-- paire :: Parser a -> Parser b -> Parser (a, b)
-- paire :: IO a -> IO b -> IO (a, b)
paire :: Monad m => m a -> m b -> m (a, b)
paire mx my = do x <- mx
                 y <- my
                 pure (x, y)

ex4 = paire getChar getChar
ex5 = paire (Just 12) (Just 'b')
ex6 = paire unCaractereQuelconque unCaractereQuelconque
