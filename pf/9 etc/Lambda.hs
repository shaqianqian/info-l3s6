module LambdaCalcul where

-- Créé par Alonzo Church dans les années 1930 pour définir ce qui est
-- calculable

-- Entiers de Church : codage des entiers sous la forme de fonctions
type Entier t = (t -> t) -> t -> t

zero :: Entier t
zero f x = x

un :: Entier t
un f x = f x

deux :: Entier t
deux f x = f (f x)

successeur :: Entier t -> Entier t
successeur n = \f x -> f (n f x)

ex1 = zero (+1) 0
ex2 = un (+1) 0
ex3 = deux (+1) 0
ex4 = successeur deux (+1) 0

addition :: Entier t -> Entier t -> Entier t
addition n m = \f x -> n f (m f x)

-- Combinateur de point fixe de Curry
-- Haskell refuse de typer un λ-terme si bizarre
-- combinateurPointFixe = \f -> (\x -> f (x x)) (\x -> f (x x))
