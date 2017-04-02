module QuelquesExemples where

{-
  Dans GHCi tester les commandes suivantes :

  2+3*4
  (2+3)*4
  sqrt 3 * 3
  head [1,2,3,4,5]
  tail [1,2,3,4,5]
  [1,2,3,4,5] !! 2
  take 3 [1,2,3,4,5]
  drop 3 [1,2,3,4,5]
  length [1,2,3,4,5]
  sum [1,2,3,4,5]
  product [1,2,3,4,5]
  [1,2,3] ++ [4,5]
  reverse [1,2,3,4,5]
-}

quadruple x = double (double x)

double x = x + x

factorielle n = product [1..n]

moyenne ns = sum ns `div` length ns
