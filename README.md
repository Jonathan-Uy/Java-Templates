# Java-Templates
Templates for common data structures and algorithms in competitive programming, written in Java.

### AAA Tree
Supports operations on a tree with weighted nodes in logarithmic time. While a Euler Tour tree can support subtree operations, and a Link-Cut Tree can support path operations, an AAA Tree can support both simultaneously. Unforunately, AAA Trees pay for this with a relatively high constant factor.
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
