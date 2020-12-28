# Java Templates
Original templates for common data structures and algorithms in competitive programming, written in Java.

## Binary Search Trees

### Size Balanced Tree
Order statistics tree with a low constant factor. Can maintain duplicate values. Select queries return the kth smallest element. Rank queries return the index of an element, starting at 1, if the tree was sorted.
| Operation             | Complexity |
|-----------------------|:----------:|
| Insert                |   O(logN)  |
| Remove                |   O(logN)  |
| Select                |   O(logN)  |
| Rank                  |   O(logN)  |

## Matrices

### Matrix Determinant
Computes the determinant of an N by N matrix in cubic time. Very fast implementation, can compute the determinant of 500 by 500 matrices in under 0.2 seconds.

### Matrix Exponentation
For an N by N matrix M, computes M to the exponent of K in O(N^3 logK) time. This template is versatile and can be modified to compute Fibonacci numbers and other linear recurrences.

## Trees

### Link Cut Tree
Supports path operations on a tree with weighted nodes in logarithmic time. Also used to solve the dynamic connectivity problem for trees.
| Operation             | Complexity |
|-----------------------|:----------:|
| Change Root           |   O(logN)  |
| Path Modification     |   O(logN)  |
| Path Increment        |   O(logN)  |
| Query Path Minimum    |   O(logN)  |
| Query Path Maximum    |   O(logN)  |
| Query Path Sum        |   O(logN)  |
| Change Parent         |   O(logN)  |
| Query LCA             |   O(logN)  |

### AAA Tree
Supports operations on a tree with weighted nodes in logarithmic time. While a Euler Tour tree can support subtree operations, and a Link-Cut Tree can support path operations, an AAA Tree can support both simultaneously. Unfortunately, AAA Trees pay for this with a relatively high constant factor.
| Operation             | Complexity |
|-----------------------|:----------:|
| Subtree Modification  |   O(logN)  |
| Change Root           |   O(logN)  |
| Path Modification     |   O(logN)  |
| Query Subtree Minimum |   O(logN)  |
| Query Subtree Maximum |   O(logN)  |
| Subtree Increment     |   O(logN)  |
| Path Increment        |   O(logN)  |
| Query Path Minimum    |   O(logN)  |
| Query Path Maximum    |   O(logN)  |
| Change Parent         |   O(logN)  |
| Query Path Sum        |   O(logN)  |
| Query Subtree Sum     |   O(logN)  |
