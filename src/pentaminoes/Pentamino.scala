package pentaminoes

import scala.util.Random
import Game.grid

class Pentamino(private var colors: Array[Array[Int]], private var edges: Array[Array[Array[Int]]]) {
  
  override def toString: String = {
    var text = ""
    for (i <- Range(0,5)) {
      text += this.colors(i).mkString(" ") + "\n"
    }
    text
  }
  
  //Returns the list of colors (ints) as 2-dimensional vector.
  def toVector: grid = this.colors.map(_.toVector).toVector
  
  /* Returns the color of given coordinate
   * Coordinates are relative to the center of pentamino. For example (0,0) means (2,2) and (-1, -2) means (1,0).
   */
  def apply(x: Int, y:Int):Int = this.toVector(x+2)(y+2)
  
  /*Returns the list of pentaminos edges as 3-dimensional vector.
   *First 2 dimension are 5*5 grid and last dimension is list of directions (int 1-4) in which a square has edges.
   */
  def edgesVector: Vector[Vector[Vector[Int]]] = this.edges.map(_.map(_.toVector).toVector).toVector
  
  //Returns the edges of square as vector. Coordinates are relative to (2,2).
  def edgesCentered(x: Int, y: Int):Vector[Int] = this.edgesVector(x+2)(y+2)
  
  //Changes the edges to diffrent type which graphics uses.
  def twoBooleanEdges: Vector[Vector[Vector[Boolean]]] = {
    val edgesArray = Array.fill(Pentamino.size, Pentamino.size, 2)(false)
    for (x <- 0 until 5; y <- 0 until 5) {
      this.edgesVector(y)(x).foreach {
        direction => 
          if (direction == 1 && x + 1 < 5) edgesArray(y)(x + 1)(1) = true
          if (direction == 2) edgesArray(y)(x)(0) = true
          if (direction == 3) edgesArray(y)(x)(1) = true
          if (direction == 4 && y + 1 < 5) edgesArray(y + 1)(x)(0) = true
      }
    }
    edgesArray.map(_.map(_.toVector).toVector).toVector
  }
  
  
  /* Rotate methods and flip methods and randomRotation all 
  both make changes to original Pentamino and return the modified version of it */

  def rotateClockwise(): Pentamino = {
    val newArray = Array.fill(5,5)(0)
    val newEdges = Array.fill(5,5)(Array(0))
    for (x <- Range(0,5); y <- Range(0,5)) {
      newArray(x)(y) = this.colors(4-y)(x)
      newEdges(x)(y) = this.edges(4-y)(x).map{Map(1->4, 4->3, 3->2, 2->1, 0->0)}
    }
    this.colors = newArray
    this.edges = newEdges
    this
  }
  
  def rotateCounterClockwise(): Pentamino = {
    val newArray = Array.fill(5,5)(0)
    val newEdges = Array.fill(5,5)(Array(0))
    for (x <- Range(0,5); y <- Range(0,5)) {
      newArray(x)(y) = this.colors(y)(4-x)
      newEdges(x)(y) = this.edges(y)(4-x).map{Map(1->2, 2->3, 3->4, 4->1, 0->0)}
    }
    this.colors = newArray
    this.edges = newEdges
    this
  }
  
  def flipVertical(): Pentamino = { // x-axis reflection
    val newArray = Array.fill(5,5)(0)
    val newEdges = Array.fill(5,5)(Array(0))
    for (x <- Range(0,5); y <- Range(0,5)) {
      newArray(x)(y) = this.colors(4-x)(y)
      newEdges(x)(y) = this.edges(4-x)(y).map{Map(1->1, 2->4, 3->3, 4->2, 0->0)}
    }
    this.colors = newArray
    this.edges = newEdges
    this
  }
  
  def flipHorizontal(): Pentamino = { // y-axis reflection
    val newArray = Array.fill(5,5)(0)
    val newEdges = Array.fill(5,5)(Array(0))
    for (x <- Range(0,5); y <- Range(0,5)) {
      newArray(x)(y) = this.colors(x)(4-y)
      newEdges(x)(y) = this.edges(x)(4-y).map{Map(1->3, 2->2, 3->1, 4->4, 0->0)}
    }
    this.colors = newArray
    this.edges = newEdges
    this
  }
  
  def randomRotation(): Pentamino = { // Randomly flip AND/OR rotate Pentamino
    Random.nextInt(4) match { // Random rotation
      case 0 => this
      case 1 => this.rotateClockwise()
      case 2 => {this.rotateClockwise(); this.rotateClockwise()}
      case 3 => this.rotateCounterClockwise()
    }
    
    Random.nextInt(4) match { // Random flip
      case 0 => this
      case 1 => this.flipHorizontal()
      case 2 => this.flipVertical()
      case 3 => {this.flipHorizontal(); this.flipVertical()}
    }

    this
  }
  
}

object Pentamino {
  
  val size = 5
  val pentaminoes: Vector[Char] = Vector('p', 'x', 'f', 'v', 'w', 'y', 'i', 't', 'z', 'u', 'n', 'l')
  
  def apply(colors: Array[Array[Int]], edges: Array[Array[Array[Int]]] ): Pentamino = new Pentamino(colors, edges)
  
  def apply(colorsList: Array[Int], edgesList: Array[Array[Int]]): Pentamino = {
    val colors = Array.ofDim[Int](5,5)
    val edges = Array.ofDim[Array[Int]](5,5)
    var edgeCounter = 0
    for (i <- 0 until colorsList.size) {
      colors(i / 5)(i % 5) = colorsList(i)
      if (colors(i / 5)(i % 5) == 0) edges(i / 5)(i % 5) = Array(0)
      else {
        edges(i / 5)(i % 5) = edgesList(edgeCounter)
        edgeCounter += 1
      }
    }
    Pentamino(colors, edges)
  }
  
  def apply(char: Char, c1: Int, c2: Int, c3: Int, c4: Int, c5: Int): Pentamino =
    char match {
      case 'p' | 'P' => this.p(c1, c2, c3, c4, c5)
      case 'x' | 'X' => this.x(c1, c2, c3, c4, c5)
      case 'f' | 'F' => this.f(c1, c2, c3, c4, c5)
      case 'v' | 'V' => this.v(c1, c2, c3, c4, c5)
      case 'w' | 'W' => this.w(c1, c2, c3, c4, c5)
      case 'y' | 'Y' => this.y(c1, c2, c3, c4, c5)
      case 'i' | 'I' => this.i(c1, c2, c3, c4, c5)
      case 't' | 'T' => this.t(c1, c2, c3, c4, c5)
      case 'z' | 'Z' => this.z(c1, c2, c3, c4, c5)
      case 'u' | 'U' => this.u(c1, c2, c3, c4, c5)
      case 'n' | 'N' => this.n(c1, c2, c3, c4, c5)
      case 'l' | 'L' => this.l(c1, c2, c3, c4, c5)
      case  _  => this.p(c1,c2,c3,c4,c5) // Default = p-pentamino
    }
  
  // Random shaped and rotated Pentamino
  def random(c1: Int, c2: Int, c3:Int, c4:Int, c5:Int): Pentamino = {
    val pentaminoChar = this.pentaminoes(Random.nextInt(pentaminoes.size))
    this(pentaminoChar, c1, c2, c3, c4, c5).randomRotation()
  }
  
  //The shapes of all diffren pentaminoes are defined below.
  
  def p(c1: Int, c2: Int, c3:Int, c4:Int, c5:Int): Pentamino = {
    Pentamino( Array( 0, 0, 0, 0, 0,
                      0, 0, c1,c2,0,
                      0, 0, c3,c4,0,
                      0, 0, c5,0, 0,
                      0, 0, 0, 0, 0 ),
               Array( Array(2,3), Array(1,2), Array(3), Array(1,4), Array(1,3,4) )
             )
  }
  
  def x(c1: Int, c2: Int, c3:Int, c4:Int, c5:Int): Pentamino = {
    Pentamino( Array( 0, 0, 0, 0, 0,
                      0, 0, c1,0, 0,
                      0, c2,c3,c4,0,
                      0, 0, c5,0, 0,
                      0, 0, 0, 0, 0),
               Array( Array(1,2,3), Array(2,3,4), Array(0), Array(1,2,4), Array(1,3,4) )
             )
  }
  
  def f(c1: Int, c2: Int, c3:Int, c4:Int, c5:Int): Pentamino = {
    Pentamino( Array( 0, 0, 0, 0, 0,
                      0, c1,c2,0, 0,
                      0, 0 ,c3,c4,0,
                      0, 0, c5,0, 0,
                      0, 0, 0, 0, 0),
               Array( Array(2,3,4), Array(1,2), Array(3), Array(1,2,4), Array(1,3,4) )
             )
  }
  
  def v(c1: Int, c2: Int, c3:Int, c4:Int, c5:Int): Pentamino = {
    Pentamino( Array( 0, 0, 0, 0, 0,
                      0, 0, 0 ,0, 0,
                      0, 0, c1,c2,c3,
                      0, 0, c4,0, 0,
                      0, 0, c5,0, 0),
               Array( Array(2,3), Array(2,4), Array(1,2,4), Array(1,3), Array(1,3,4) )
             )
  }
  
  def w(c1: Int, c2: Int, c3:Int, c4:Int, c5:Int): Pentamino = {
    Pentamino( Array( 0, 0, 0, 0, 0,
                      0, c1,0 ,0, 0,
                      0, c2,c3,0, 0,
                      0, 0, c4,c5,0,
                      0, 0, 0, 0, 0),
               Array( Array(1,2,3), Array(3,4), Array(1,2), Array(3,4), Array(1,2,4) )
             )
  }
  
  def y(c1: Int, c2: Int, c3:Int, c4:Int, c5:Int): Pentamino = {
    Pentamino( Array( 0, 0, 0, 0, 0,
                      0, 0, c1,0, 0,
                      0, 0, c2,c3,0,
                      0, 0, c4,0, 0,
                      0, 0, c5,0, 0),
               Array( Array(1,2,3), Array(3), Array(1,2,4), Array(1,3), Array(1,3,4) )
             )
  }
  
  def i(c1: Int, c2: Int, c3:Int, c4:Int, c5:Int): Pentamino = {
    Pentamino( Array( 0, 0, c1,0, 0,
                      0, 0, c2,0, 0,
                      0, 0, c3,0, 0,
                      0, 0, c4,0, 0,
                      0, 0, c5,0, 0),
               Array( Array(1,2,3), Array(1,3), Array(1,3), Array(1,3), Array(1,3,4) )
             )
  }
  
  def t(c1: Int, c2: Int, c3:Int, c4:Int, c5:Int): Pentamino = {
    Pentamino( Array( 0, 0, 0 ,0, 0,
                      0, c1,c2,c3,0,
                      0, 0, c4,0, 0,
                      0, 0, c5,0, 0,
                      0, 0, 0, 0, 0),
               Array( Array(2,3,4), Array(2), Array(1,2,4), Array(1,3), Array(1,3,4) )
             )
  }
  
  def z(c1: Int, c2: Int, c3:Int, c4:Int, c5:Int): Pentamino = {
    Pentamino( Array( 0, 0, 0 ,0, 0,
                      0, c1,c2,0, 0,
                      0, 0, c3,0, 0,
                      0, 0, c4,c5,0,
                      0, 0, 0, 0, 0),
               Array( Array(2,3,4), Array(1,2), Array(1,3), Array(3,4), Array(1,2,4) )
             )
  }
  
  def u(c1: Int, c2: Int, c3:Int, c4:Int, c5:Int): Pentamino = {
    Pentamino( Array( 0, 0, 0 ,0, 0,
                      0, c1,0, c2,0,
                      0, c3,c4,c5,0,
                      0, 0, 0 ,0, 0,
                      0, 0, 0, 0, 0),
               Array( Array(1,2,3), Array(1,2,3), Array(3,4), Array(2,4), Array(1,4) )
             )
  }
  
  def n(c1: Int, c2: Int, c3:Int, c4:Int, c5:Int): Pentamino = {
    Pentamino( Array( 0, 0, 0 ,0, 0,
                      0, 0, 0, c1,0,
                      0, 0, c2,c3,0,
                      0, 0, c4,0, 0,
                      0, 0, c5,0, 0),
               Array( Array(1,2,3), Array(2,3), Array(1,4), Array(1,3), Array(1,3,4) )
             )
  }
  
  def l(c1: Int, c2: Int, c3:Int, c4:Int, c5:Int): Pentamino = {
    Pentamino( Array( 0, 0, 0 ,0, 0,
                      0, 0, c1,c2,0,
                      0, 0, c3,0, 0,
                      0, 0, c4,0, 0,
                      0, 0, c5,0, 0),
               Array( Array(2,3), Array(1,2,4), Array(1,3), Array(1,3), Array(1,3,4) )
             )
  }
  
}

