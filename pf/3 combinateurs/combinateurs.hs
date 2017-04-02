module Combinateurs where
-- Aussi appelés « Fonctions d’ordre supérieur »

import Data.List (nub,sort)
import Data.Char (isLower)

add :: Int -> Int -> Int
add x y = x + y

-- application partielle
ex1 :: Int -> Int
ex1 = add 1

deuxFois :: (a -> a) -> a -> a
deuxFois f x = f (f x)

ex2 :: Num a => a -> a
ex2 = deuxFois (+1)

ex3 = ex2 3

stupide :: [a] -> [a]
stupide = deuxFois reverse

map' :: (a -> b) -> [a] -> [b]
map' f xs = [ f x | x <- xs ]

map'' _     [] = []
map'' f (x:xs) = f x : map'' f xs

ex4 = reverse ["auie", "tdpob"]
ex5 = map reverse ["auie", "tdpob"]
ex6 = map (map (1+)) [[1..10], [2,4..10]]
ex7 = sum (map sum [[1..10], [2,4..10]])
ex8 = map isLower "autieBÉPBÉPTRλ"
ex9 = et ex8

et :: [Bool] -> Bool
et [] = True
et (x:xs) = x && et xs

filter' :: (a -> Bool) -> [a] -> [a]
filter' p xs = [ x | x <- xs, p x ]

filter'' _     []             = []
filter'' p (x:xs) | p x       = x : filter'' p xs
                  | otherwise = filter'' p xs

ex10 = filter isLower "autieBÉPBÉPTRλ"
ex11 = filter even [0..10]

qsortBy :: (a -> a -> Ordering) -> [a] -> [a]
qsortBy _       [] = []
qsortBy cmp (x:xs) = qsortBy cmp petits
                  ++ [x]
                  ++ qsortBy cmp grands
    where petits = [ y | y <- xs, cmp y x == LT ]
          grands = [ z | z <- xs, cmp z x /= LT ]

ex12 = qsortBy compare [4,2,5,1,65,4]
-- Voir toutes les fonctions ...By dans Data.List

ordreDecroissant x y = case compare x y of
                         LT -> GT
                         EQ -> EQ
                         GT -> LT

ex13 = qsortBy ordreDecroissant [4,2,5,1,65,4]
ex14 = qsortBy (\x y -> compare y x) [4,2,5,1,65,4]

retourneArguments :: (a -> b -> c) -> b -> a -> c
retourneArguments f x y = f y x

ex15 = qsortBy (retourneArguments compare) [4,2,5,1,65,4]
ex16 = qsortBy (flip compare) [4,2,5,1,65,4]

ex17 :: Int -> Bool -> Char -> [a]
ex17 = undefined
-- juste pour montrer flip ex17, ex17 n’est pas définissable de façon
-- intéressante

somme [] = 0
somme (x:xs) = x + somme xs

produit [] = 1
produit (x:xs) = x * produit xs

generique :: (a -> b -> b) -> b -> [a] -> b
generique _ v []     = v
generique f v (x:xs) = x `f` generique f v xs

somme'   = generique (+) 0
produit' = generique (*) 1
et'      = generique (&&) True

-- generique est foldr
-- cf. Foldable

-- repli en commençant à gauche
-- le deuxième argument est un accumulateur
monFoldl _ v [] = v
monFoldl f v (x:xs) = monFoldl f (v `f` x) xs

ex18 = foldr (^) 2 [2,2,2]
ex19 = foldl (^) 2 [2,2,2]

-- Composition de fonctions
-- Premier combinateur !
o :: (b -> c) -> (a -> b) -> (a -> c)
f `o` g = \x -> f (g x)

pair :: Int -> Bool
pair = even

impair :: Int -> Bool
impair = not . pair

deuxFois' f = f . f


-- avec un fold
longueur :: [a] -> Int
-- longueur = foldr (\_ r -> r + 1) 0
-- longueur = foldr (\_ -> (1+)) 0
longueur = foldr (const (1+)) 0

longueur' = sum . map (const 1)

-- très similaire à 
-- sort | uniq | head
-- dans le shell
ex20 :: Ord a => [a] -> [a]
ex20 = take 10 . nub . sort
-- ex20 xs = take 10 (nub (sort xs))
