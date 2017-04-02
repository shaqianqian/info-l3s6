module Paresse where

-- Ordre d’évaluation
-- Paresse est un abus de langage pour dire que Haskell est
-- un langage à évaluation non-stricte

import System.Environment

carre n = n * n

ex1 = carre (1+2)

-- Deux évaluations possibles
--
-- Appel par valeur (call by value) : arguments évalués avant l’appel
-- Évaluation stricte
-- carre (1+2) = carre 3
--             = 3 * 3
--             = 9
--
-- Appel par nom (call by name) : arguments passés tels quels
-- carre (1+2) = (1+2) * (1+2)
--             = 3 * (1+2)
--             = 3 * 3
--             = 9
--
-- 9 est la _forme normale_
-- Toujours le même résultat, quel que soit l’ordre
-- d’évaluation dans du code pur

trouNoir = trouNoir

ex2 = trouNoir
ex3 = fst (12, trouNoir)
ex4 = snd (12, trouNoir)

ifThenElse :: Bool -> a -> a -> a
ifThenElse True  t _ = t
ifThenElse False _ e = e

ex5 = ifThenElse True  trouNoir 12
ex6 = ifThenElse False trouNoir 12

ex7  = trouNoir && True
ex8  = trouNoir && False
ex9  = True     && trouNoir
ex10 = False    && trouNoir
-- ex10 est le seul qui se termine

(&&&) :: Bool -> Bool -> Bool
_ &&& False = False
b &&& True  = b

ex11 = trouNoir &&& True
ex12 = trouNoir &&& False
ex13 = True     &&& trouNoir
ex14 = False    &&& trouNoir
-- ex12 est le seul qui se termine

-- Appel au besoin (call by need)
-- (le let ... in est un peu similaire à un
-- environnement)
-- carre (1+2) = let n = 1+2 in n * n
--             = let n = 3 in n * n
--             = 9

exFonct n = map (\x -> n + x)

boum  = [1, 2, trouNoir]
boum' = [1, 2, undefined]

long   = [0..1000000]
quatre = long !! 4

uns   = 1 : uns
uns'  = repeat 1
uns'' = iterate (const 1) 1

fib :: [Integer]
fib = 1 : 1 : zipWith (+) fib (tail fib)

-- Construit un exécutable calculant le n^e nombre de Fibonacci : montre que GHC
-- crée un exécutable utilisant très peu de mémoire (il se rend compte que les
-- valeurs intermédiaires ne sont pas nécessaires dès que le nombre de Fibonacci
-- suivant est calculé), alors que l’évaluation dans GHCi mémorise tous les
-- éléments de la liste qui ont été calculés
-- À comparer donc avec GHCi, en tapant :set +s et en regardant la
-- mémoire allouée par GHCi après le calcul de fib !! 100000 (si vous
-- avez assez de mémoire sur votre machine ! Il prend 1,5Go chez moi)
mainFibo :: IO ()
mainFibo = do (n:_) <- getArgs
              print (fib !! read n)

plus1 = map (1+) [0..]

ex15 = let _ = plus1 !! 30
       in  12
ex16 = plus1 !! 30 `seq` 12
-- seq est strict en ses deux arguments

-- Crible d’Ératosthène
premiers :: [Integer]
premiers = crible [2..]
    where crible (n:ns) = n : crible (filter (\n' -> n' `mod` n /= 0) ns)

-- Découplage entre le contrôle et les données
premiersInfCent = takeWhile (< 100) -- contrôle
                    premiers        -- données
-- Il est difficile d’écrire un crible qui génère exactement les 100
-- premiers nombres premiers : on ne sait pas, a priori, jusqu’où il
-- faut cribler ; la paresse permet la liste infinie des nombres
-- premiers, qui nous fera cribler juste ce qu’il faut
centPremiers = take 100   -- contrôle
                 premiers -- données

-- Méthode de Héron du calcul de la racine carrée de n :
-- la suite u_i+1 = (u_i + n/u_i) / 2 converge rapidement vers la
-- racine carrée de n

-- On définit d’abord la suite
heron :: Double -> [Double]
heron n = iterate f 1
    where f a = (a + n / a) / 2

-- On définit ensuite un critère d’arrêt
arret :: Double -> [Double] -> Double
arret ecart (a:b:_) | abs (a - b) < ecart = b
arret ecart (_:bs)                        = arret ecart bs

ex17 :: Double
ex17 = arret 0.001 -- contrôle
         (heron 2) -- donnée

-- On peut définir un autre critère d’arrêt, sans modifier le code qui
-- génère la suite
arretRapport :: Double -> [Double] -> Double
arretRapport ecart (a:b:_) | abs (a/b - 1) < ecart = b
arretRapport ecart (_:bs)                          = arretRapport ecart bs

ex18 = arretRapport 0.000001 -- contrôle
         (heron 2)           -- donnée
