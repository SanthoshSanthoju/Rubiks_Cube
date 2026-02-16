# Rubik's Cube Solver

A **Java-based Rubik's Cube solver** featuring 3 cube representations and 4 graph-search algorithms — from brute-force DFS to optimal IDA\* with a pre-computed Corner Pattern Database heuristic.

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Algorithms](https://img.shields.io/badge/Algorithms-DFS%20%7C%20BFS%20%7C%20IDDFS%20%7C%20IDA*-blue?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Working-brightgreen?style=for-the-badge)

---

## Features

- **3 Cube Representations** with different space-time tradeoffs
  - 3D Array (`char[6][3][3]`) — intuitive and easy to debug
  - 1D Array (`char[54]`) — cache-friendly flat layout
  - Bitboard (`long[6]`) — blazing fast with bitwise operations
- **4 Solving Algorithms**, progressively more powerful
  - DFS, BFS, IDDFS, and IDA\* (with heuristic pruning)
- **Corner Pattern Database** — pre-computed heuristic (~50 MB) for optimal solving
- **Clean OOP Design** — abstract base class with polymorphic solvers

---

## Architecture

```
rubiks-cube-solver/
├── java/
│   ├── Main.java                          # Driver — tests all representations & solvers
│   ├── model/
│   │   ├── RubiksCube.java                # Abstract base class (enums, move dispatch, corner helpers)
│   │   ├── RubiksCube3dArray.java         # char[6][3][3] representation
│   │   ├── RubiksCube1dArray.java         # char[54] representation
│   │   └── RubiksCubeBitboard.java        # long[6] bitboard representation
│   ├── solver/
│   │   ├── DFSSolver.java                 # Depth-First Search
│   │   ├── BFSSolver.java                 # Breadth-First Search
│   │   ├── IDDFSSolver.java               # Iterative Deepening DFS
│   │   └── IDAstarSolver.java             # IDA* with pattern database heuristic
│   └── patterndb/
│       ├── PatternDatabase.java           # Abstract pattern database with NibbleArray
│       ├── CornerPatternDatabase.java     # Corner-specific index computation
│       ├── CornerDBMaker.java             # BFS-based database generator
│       ├── PermutationIndexer.java        # O(N) Lehmer code ranking
│       ├── NibbleArray.java               # 4-bit packed array (halves memory)
│       └── MathUtils.java                 # Factorial, nPk, nCk utilities
└── Databases/
    └── cornerDepth5V1.txt                 # Pre-computed corner pattern database
```

---

## Getting Started

### Prerequisites

- Java 17 or higher
- `javac` and `java` available in PATH

### Compile & Run

```bash
cd java
javac -d . Main.java model/*.java solver/*.java patterndb/*.java
java Main
```

On first run with IDA\*, the corner database will be generated automatically if not found (~a few minutes).

### Sample Output

```
=== Rubik's Cube Solver (Java) ===

--- Testing Cube Representations ---
3dArray solved: true
1dArray solved: true
Bitboard solved: true

--- DFS Solver (max depth 8, shuffle 4 moves) ---
Shuffle: R2 U' F L
Solution (4 moves): L' F' U R2
Solved: true

--- IDA* Solver (with Corner Pattern Database) ---
Shuffle: B2 L R' U F2 D' R
Solution (7 moves): R' D F2 U' R L' B2
Solved: true
```

---

## How It Works

### The Cube as a Graph Problem

| Concept | Mapping |
|---------|---------|
| **Node** | A cube state (one of ~4.3 × 10¹⁹ possible) |
| **Edge** | A single move (18 possible per state) |
| **Goal** | Shortest path from scrambled state → solved state |

### Cube Representations

| Representation | Storage | `isSolved()` | `copy()` | `hashCode()` | Best For |
|----------------|---------|--------------|----------|---------------|----------|
| **3D Array** | `char[6][3][3]` | O(54) | O(54) | String-based | Debugging |
| **1D Array** | `char[54]` | O(54) | O(54) | String-based | Readability |
| **Bitboard** | `long[6]` | O(6) comparisons | O(6) | XOR of 6 longs | **Solvers** |

The **Bitboard** stores each face as a 64-bit `long` using one-hot color encoding. Face rotation becomes a simple bit-shift:

```java
// 90° clockwise = shift left by 16 bits + wrap
long top = side >>> 48;
bitboard[ind] = (side << 16) | top;
```

### Solver Algorithms

| Algorithm | Optimal? | Memory | Practical Limit | Key Idea |
|-----------|----------|--------|-----------------|----------|
| **DFS** | No | O(d) | ~4 moves | Dive deep, backtrack |
| **BFS** | Yes | O(18^d) | ~4 moves | Level-by-level exploration |
| **IDDFS** | Yes | O(d) | ~5 moves | DFS memory + BFS optimality |
| **IDA\*** | Yes | O(d) | **~7+ moves** | Heuristic pruning with pattern DB |

### Corner Pattern Database

The IDA\* solver uses a pre-computed lookup table of **88 million entries**, each storing the minimum moves needed to solve the cube's 8 corners from that configuration.

**How it's built:**
1. BFS outward from the **solved state** to depth 8
2. For each state encountered, compute a unique index:
   - **Permutation rank** via Lehmer code (0 to 40,319)
   - **Orientation number** in base-3 (0 to 2,186)
   - `index = rank × 2187 + orientationNum`
3. Store the depth (0–8) in a **NibbleArray** (4 bits per entry, halving memory to ~50 MB)

**At solve time:** Look up `h(n)` for any cube state in O(1). If `depth + h(n) > bound`, prune the branch entirely.

---

##  Algorithm Complexity

```
States explored for a 7-move scramble:

BFS:   ~18⁷ ≈ 612,000,000 states (impractical)
IDA*:  ~tens of thousands       (heuristic pruning!)
       ━━━━━━━━━━━━━━━━━━━━━━━━
       Orders of magnitude fewer
```

---



---

##  Future Improvements

- [ ] Edge pattern database for stronger heuristics
- [ ] Multi-heuristic IDA\* using `max(h_corners, h_edges)`
- [ ] Move pruning to avoid redundant sequences (e.g., U followed by U')
- [ ] Symmetry reduction to exploit the cube's 48-fold symmetry group
- [ ] Interactive GUI / web visualization
- [ ] Webcam-based cube scanner

---

## References

- [Korf's Algorithm (IDA\*)](https://en.wikipedia.org/wiki/Iterative_deepening_A*)
- [Pattern Databases for Rubik's Cube](https://www.cs.princeton.edu/courses/archive/fall06/cos402/papers/korsweb.pdf)
- [Lehmer Code](https://en.wikipedia.org/wiki/Lehmer_code)
- [God's Number is 20](https://www.cube20.org/)

---


