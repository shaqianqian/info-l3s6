somme n  = sum [1..n]
somme' n = sum (enumFromTo 1 n)

sommePairs n   = sum [2,4..2*n]
sommePairs' n  = sum (enumFromThenTo 2 4 (2*n))
sommePairs'' n = sum (map (* 2) [1..n])

sommeCarres n = sum (map (^ 2) [1..n])
