module Types where

a :: Bool
a = True

c :: Char
c = 'λ'

n :: Int
n = 12

borne :: Int
borne = maxBound

n' :: Integer
n' = 9223372036854775807 + 1

f :: Float
f = 1.2

d :: Double
d = 34.4

s :: String
s = "abc"

s' :: [Char]
s' = ['a', 'b', 'c']

tousChars = ['\NUL'..]

-- Fonctions

add :: (Int, Int) -> Int
add (x,y) = x + y

zeroTo :: Int -> [Int]
zeroTo n = [0..n]

duplique :: Int -> (Int, Int)
duplique n = (n, n)

add' :: Int -> (Int -> Int)
add' x y = x + y

exAdd' = (add' 1) 2

add3 :: Int -> Int -> Int -> Int
add3 x y z = x + y + z

t :: [a] -> [a]
t = take 3

longueur :: [a] -> Int
longueur = length

-- Fonctions polymorphes

premier :: (a,b) -> a
premier (x,y) = x

premier' = fst

tete :: [a] -> a
tete = head

identite :: a -> a
identite x = x

-- Quelles fonctions ont les types :
-- [a] -> a
ex1 = head
ex2 xs = xs !! 14

-- [a] -> [a]
-- on peut juste mélanger les éléments, en supprimer

-- Classes de type :
-- Num, Eq, Ord, Fractional, etc.

ex3 = sum [1, 2, 3]
ex4 = sum [1.1, 2.2, 3]
ex5 = [1.1, 2.2, 3]
-- ex5 = sum [False]

n1 :: Float
n1 = 1.1

n2 :: Double
n2 = 2.2

-- On ne peut pas mettre n1 et n2 dans une même liste :
-- ex6 = [n1, n2]

ex6' :: [Double]
ex6' = [1.1, n2]

ex6'' :: Fractional a => [a]
ex6'' = [1.1, 2.2]

n3 :: Num a => a
n3 = 12

-- Si n3 avait le type Integer, l’expression suivante serait mal
-- typée :
n4 = n3 + 2.3
