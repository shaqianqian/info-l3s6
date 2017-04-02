module Mystere where

import Data.List (sort)
import Test.QuickCheck

myst :: Ord a => [a] -> [a]
myst     [] = []
myst (x:xs) = myst ys ++ [x] ++ myst zs
    where ys = [ y | y <- xs, y < x]
          zs = [ z | z <- xs, z >= x]

prop_myst :: Ord a => [a] -> Bool
prop_myst xs = sort xs == myst xs

-- dans GHCi, lancer :
-- quickCheck prop_myst
