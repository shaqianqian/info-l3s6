module DéclarationTypes where

-- Synonymes

-- type String = [Char]
type Point = (Float, Float)

bougeAGauche :: Point -> Point
-- identique à (mais plus lisible que) :
-- bougeAGauche :: (Float, Float) -> (Float, Float)
bougeAGauche (x, y) = (x-1, y)

-- type Regles = Symbole -> Mot

-- On peut réusiner le code en modifiant les synonymes de types
-- type Point = (Float, Float, Float)
-- et en se laissant guider par le compilateur

-- Synonymes paramétrés
type Paire a = (a, a)

duplique :: a -> Paire a
duplique x = (x, x)

ex1 :: Paire Float -> Paire Float
ex1 = bougeAGauche

-- Peuvent être imbriqués
type Rayon = Float
type Cercle = (Point, Rayon)

-- mais pas récursif
-- type Tree a = (a, [Tree a])


-- Types de données algébriques

data Bool' = Vrai
           | Faux
    deriving Show

show' :: Bool' -> String
show' Vrai = "Vrai"
show' Faux = "Faux"

-- Vrai et Faux sont les constructeurs du type Bool'

data Ordering' = LT | EQ | GT

data Reponse = Oui
             | Non
             | PtetBenQuOuiPtetBenQuNon
    deriving Show

contredit :: Reponse -> Reponse
contredit Oui                      = Non
contredit Non                      = Oui
contredit PtetBenQuOuiPtetBenQuNon = PtetBenQuOuiPtetBenQuNon

data TupleEntiers = Paire Int Int
                  | Triplet Int Int Int
    deriving (Show, Eq, Ord)

ex2 = Paire 12 34

réduit :: TupleEntiers -> Int
réduit (Paire n n')       = n + n'
réduit (Triplet n n' n'') = n + n' + n''

-- Type Somme
data Message = Chaine String
             | Entier Integer
    deriving Show

ex3 = Chaine "12"
ex4 = Entier 34
-- Il est ainsi possible de mettre des « types différents »
-- dans une liste en créant un type somme
ex5 = [ex3, ex4]

data PeutEtre a = Rien
                | Juste a
    deriving (Show, Eq)

-- cf Maybe

teteSure :: [a] -> Maybe a
teteSure []    = Nothing
teteSure (x:_) = Just x

commenceParA :: String -> Bool
commenceParA ('A':_) = True
commenceParA _       = False

commenceParA' :: String -> Bool
commenceParA' cs = not (null cs) && head cs == 'A'

-- Cette variante va crasher sur la liste vide
commenceParA'' :: String -> Bool
commenceParA'' cs = -- sauf si on ajoute : not (null cs) &&
                    case head cs of
                        'A' -> True
                        _   -> False

commenceParA''' :: String -> Bool
commenceParA''' cs = case teteSure cs of
                        Just 'A' -> True
                        _        -> False

-- Data récursif

data Liste a = Vide
             | Cons a (Liste a)
    deriving (Show, Eq)

ex6 = Vide
ex7 = Cons 12 Vide
ex8 = Cons 34 (Cons 12 Vide)

convertit :: [a] -> Liste a
convertit [] = Vide
convertit (x:xs) = Cons x (convertit xs)

convertit' :: [a] -> Liste a
convertit' = foldr Cons Vide

data Liste' a = Vide'
              | a :- Liste' a
    deriving (Show, Eq)

infixr 5 :-

-- Variante de l’addition avec une plus faible priorité
(+++) :: Integer -> Integer -> Integer
(+++) = (+)

infixl 1 +++


ex9 = 12 :- Vide'
ex10 = 12 :- 34 :- 56 :- Vide'
ex11 = 12 :- (34 :- (56 :- Vide'))
ex12 = 12 + 34 :- Vide'
-- Parenthèses indispensables parce que (+++) est moins
-- prioritaire que (:-)
ex13 = (12 +++ 34) :- Vide'

data Arbre a = Noeud a [Arbre a]
    deriving (Show, Eq)

ex14 = Noeud 12 []
ex15 = Noeud 34 [ex14,ex14,ex14]
ex16 = Noeud 56 [ex14,ex15,ex16]

mapArbre :: (a -> b) -> Arbre a -> Arbre b
mapArbre f (Noeud x xs) = Noeud (f x) (map (mapArbre f) xs)

data Arbre' a = Noeud' { valeur :: a
                       , fils   :: [Arbre' a] }
    deriving (Show, Eq)

ex17 = Noeud' { valeur = 12, fils = [] }
ex18 = Noeud' { fils = [], valeur = 34 }
ex19 = Noeud' 56 [ex18]
ex20 = ex19 { fils = [ex17,ex17] }
ex21 = valeur ex20
