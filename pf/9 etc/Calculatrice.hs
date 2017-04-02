-- |
-- Module      :  Calculatrice
-- License     :  CeCILL
--
-- Stability   :  experimental
-- Portability :  unknown
--
-- On reprend l’analyseur d’expressions vu pendant le cours Analyseur
-- syntaxique, en l’enrichissant pour avoir les 4 opérations, en le
-- récrivant en utilisant la notation `do` et en évitant les étapes de
-- « backtracking » inutiles dans `facteurExp` et `termeExp`.

module Calculatrice where

import Data.Char (isDigit)
import Parser
import Control.Monad (join)

data Expression a = Nombre a
                  | Add   (Expression a) (Expression a)
                  | Soust (Expression a) (Expression a)
                  | Mult  (Expression a) (Expression a)
                  | Div   (Expression a) (Expression a)
    deriving (Show, Eq, Read)

type ParserExpr = Parser (Expression Integer)

nombreExp :: ParserExpr
nombreExp = pure . Nombre . read =<< some (carQuand isDigit)

facteurExp :: ParserExpr
facteurExp = nombreExp
         <|> ( do car '('
                  exp <- expressionExp
                  car ')'
                  pure exp )

termeExp :: ParserExpr
termeExp = do f <- facteurExp
              (    do car '*'
                      t <- termeExp
                      pure (Mult f t)
               <|> do car '/'
                      t <- termeExp
                      pure (Div f t)
               <|> pure f )

expressionExp :: ParserExpr
expressionExp = do t <- termeExp
                   (    do car '+'
                           e <- expressionExp
                           pure (Add t e)
                    <|> do car '-'
                           e <- expressionExp
                           pure (Soust t e)
                    <|> pure t )

testParser :: String -> String
testParser = unlines . map (show . runParser expressionExp) . lines

-- evalExpr :: Expression Integer -> Maybe Integer
-- evalExpr (Nombre n)   = Just n
-- evalExpr (Add e e')   = evalExpr e + evalExpr e'
-- evalExpr (Soust e e') = evalExpr e - evalExpr e'
-- evalExpr (Mult e e')  = evalExpr e * evalExpr e'
-- evalExpr (Div e e')   = case evalExpr e' of
--                           Nothing -> Nothing
--                           Just 0  -> Nothing
--                           Just r' ->
--                             case evalExpr e of
--                               Nothing -> Nothing
--                               Just r  -> Just (r `div` r')

evalExpr :: Expression Integer -> Maybe Integer
evalExpr (Nombre n)   = Just n
evalExpr (Add e e')   = (+) <$> evalExpr e <*> evalExpr e'
evalExpr (Soust e e') = (-) <$> evalExpr e <*> evalExpr e'
evalExpr (Mult e e')  = (*) <$> evalExpr e <*> evalExpr e'
evalExpr (Div e e')   =
    div <$> evalExpr e <*> filter0 (evalExpr e')
    where filter0 (Just 0) = Nothing
          filter0 r        = r

safeResultat :: Resultat a -> Maybe a
safeResultat Nothing       = Nothing
safeResultat (Just (r, _)) = Just r

-- Maybe est une instance de la classe de type Functor

safeResultat' :: Resultat a -> Maybe a
safeResultat' = fmap fst

sousJust :: (a -> b) -> Maybe a -> Maybe b
sousJust _ Nothing  = Nothing
sousJust f (Just r) = Just (f r)
-- Comparer avec (a -> b) -> [a] -> [b]
-- Il s’agit de la fonction fmap de la classe Functor ; cette fonction
-- a le synonyme (<$>) pour une utilisation infixe

-- Pour nos exemples avec (+), on aurait en fait besoin de
-- fmap2 :: (a -> b -> c) -> Maybe a -> Maybe b -> Maybe c
-- Pour éviter de définir de nombreuses fonctions spécifiques, on
-- voudrait une fonction de type Maybe (a -> b) -> Maybe a -> Maybe b

nouvelleFonction :: Maybe (a -> b) -> Maybe a -> Maybe b
nouvelleFonction (Just f) (Just a) = Just (f a)
nouvelleFonction _        _        = Nothing

calculatrice :: String -> String
calculatrice = unlines . map (show . join . fmap evalExpr . safeResultat' . runParser expressionExp) . lines


main :: IO ()
main = interact calculatrice
