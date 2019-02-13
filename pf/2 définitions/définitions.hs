module DéfinitionsFonctions where

-- Définitions avec if-then-else ou des gardes

absolue :: (Num a, Ord a) => a -> a
absolue n = if n >= 0
                then n
                else -n

signe :: (Ord a, Num a, Num t) => a -> t
signe n = if n > 0
            then 1
            else if n == 0
                    then 0
                    else -1

-- Définition alternative avec des gardes
signe' n | n > 0     = 1
         | n == 0    = 0
         | otherwise = -1
         -- | True      = -1

-- if-then-else sont des expressions, donc peuvent être utilisées
-- comme des sous-expressions
pairSuivant n = n + 1 + if n `mod` 2 == 0 then 1 else 0



-- Filtrage de motifs

non :: Bool -> Bool
non False = True
non True  = False

(&&&) :: Bool -> Bool -> Bool
True  &&& True  = True
False &&& True  = False
True  &&& False = False
False &&& False = False

(&&&&) :: Bool -> Bool -> Bool
-- (&&&&) True True  = True
True &&&& True  = True
_    &&&& _     = False

-- && droit : évalue d’abord son argument de droite
(&&&&&) :: Bool -> Bool -> Bool
b &&&&& True = b
_ &&&&& _    = False

(&&&&&&) :: Bool -> Bool -> Bool
b &&&&&& b' | b == b' = b
_ &&&&&& _            = False


tete (x:_) = x

decompose (x:xs) = (x, xs)

extraitTete tous@(x:_) = (x, tous)
-- est équivalent à
extraitTete' (x:xs) = (x, (x:xs))

somme (0:xs) = sum xs
somme xs     = sum xs

troisieme (x:(y:(z:xs))) = z

troisieme' (_:_:z:_) = z

cas []        = "zéro"
cas [_]       = "un"
cas [_,_]     = "deux"
cas (_:_:_:_) = "trois ou plus"
-- ou alors, plus court :
-- cas _ = "trois ou plus"

essayons :: [[t]] -> (t, [t], [[t]])
essayons ((x:ys):zss) = (x, ys, zss)

eclair []  _ = []
eclair _  [] = []
eclair (x:xs) (y:ys) = (x,y) : eclair xs ys


-- Exercice

messageConnexion :: [(String, String)] -> String -> String
messageConnexion loginsNoms "root" = "Bienvenue Maître"
messageConnexion loginsNoms login  = go loginsNoms
    where go [] = "Bienvenue étranger"
          go ((log, nom):logNoms) | log == login = "Bonjour " ++ nom
                                  | otherwise    = go logNoms

-- exemples :
-- messageConnexion [("xk", "Robert")] "root"  = "Bienvenue Maître"
-- messageConnexion [("xk", "Robert")] "xk"    = "Bonjour Robert"
-- messageConnexion [("xk", "Robert")] "autre" = "Bienvenue étranger"


-- Fonction anonyme

impairs = let f x = 2 * x + 1
          in  map f [0..]

impairs' = map f [0..]
    where f x = 2 * x + 1

impairs'' = map (\x -> 2 * x + 1) [0..]

f n = replicate n 'a' ++ "h !"
g = \n -> replicate n 'a' ++ "h !"

curryfie f = \x y -> f (x,y)

mult n ns = map (\n' -> n' * n) ns

mult' :: Num a => a -> [a] -> [a]
mult' n = map (\n' -> n' * n)

-- Sections : application partielle du premier _ou_ deuxième argument
-- d’un opérateur

mult'' n = map (* n)

puissances = map (2^) [0..]
carres     = map (^2) [0..]
