package gui

import pentaminoes._
import Game.grid
import scala.swing._
import scala.swing.event._
import java.awt.Color
import scala.swing.GridBagPanel._
import javax.swing.{ UIManager, ImageIcon }
import java.io.File
import javax.sound.sampled.AudioSystem

private object GameWindow extends SimpleSwingApplication {
  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName)

  var Grid = Game.grid

  
  //Variables defined 
  val gridSize = Grid.size
  val blockSize = 50
  val smallBlockSize = 25
  val gridDimesnion = new Dimension(gridSize * blockSize, gridSize * blockSize)
  val nextGridSize = 5
  val nextGridDimension = new Dimension(nextGridSize * smallBlockSize, nextGridSize * smallBlockSize)
  val windowSize = new Dimension(1000, 900)
  var mousePosx = 3
  var mousePosy = 3

  //ImageIcons as pictures
  val verticalPic =         new ImageIcon("Icons/flipVertical.png")
  val horizontalPic =       new ImageIcon("Icons/flipHorizontal.png")
  val clockwisePic =        new ImageIcon("Icons/rotateClockwise.png")
  val counterclockwisePic = new ImageIcon("Icons/rotateCounterclockwise.png")
  val backgroundPic =       new ImageIcon("Icons/background.png")

  val defaultFont = new Font("Castellar", 0, 30)

  //Main game grid
  val grid = new Display(gridSize, gridSize, Grid.colors, Grid.edges, blockSize)

  //The next 2 pentaminoes displayed
  val currentPentamino = new Display(nextGridSize, nextGridSize, Game.currentPentamino.toVector,
    Game.currentPentamino.twoBooleanEdges, smallBlockSize)

  val nextPentamino = new Display(nextGridSize, nextGridSize, Game.nextPentamino.toVector,
    Game.nextPentamino.twoBooleanEdges, smallBlockSize)

  //Text strings for the scoreboard in game
  def scoreText = "Score: " + Game.score
  def levelText = "Level: " + Game.level
  def rowsText  = "Rows: " + Game.rowsToNextLevel
  
  //Update methods for game elements
  def updateLabels() = {
    score.text = scoreText
    level.text = levelText
    rows.text  = rowsText
  }
  def updateHighscores() = {
    val scores = Highscore.getHighscoreListAsString
    for (i <- 0 until highscores.size) {
      highscores(i).text = s"${i + 1}: ${scores(i)}"
      highscores(i).foreground = Color.WHITE
    }
  }

  def updateGrids() = {
    grid.colors = Grid.colors
    grid.edges  = Grid.edges
    currentPentamino.colors = Game.currentPentamino.toVector
    currentPentamino.edges  = Game.currentPentamino.twoBooleanEdges
    nextPentamino.colors = Game.nextPentamino.toVector
    nextPentamino.edges  = Game.nextPentamino.twoBooleanEdges
  }

  
  //Shows what the move would do if the next pentamino was placed on the current position
  def showHypo() = {
    def hypoGrid = Grid.hypotheticalAdd(Game.currentPentamino, mousePosx, mousePosy)
    grid.colors = hypoGrid.colors
    grid.edges  = hypoGrid.edges
  }
 
  //Scoreboard created here
  val score = new Label { text = scoreText; preferredSize = new Dimension(250, 45); font = defaultFont }
  val level = new Label { text = levelText; preferredSize = new Dimension(200, 45); font = defaultFont }
  val rows  = new Label { text = rowsText; preferredSize = new Dimension(250, 45); font = defaultFont }

  val scoreBoard = new FlowPanel {
    contents += score
    contents += level
    contents += rows
  }
  
  //Array to score highscores within labels
  val highscores =
    Array.fill[Label](Highscore.getHighscoreList.size)(new Label { font = defaultFont; foreground = Color.WHITE })

  //Buttons created here
  val flipHorizontally = new Button {
    preferredSize = new Dimension(100, 100)
    icon = horizontalPic
  }

  val flipVertically = new Button {
    preferredSize = new Dimension(100, 100)
    icon = verticalPic
  }

  val rotateClockwise = new Button {
    preferredSize = new Dimension(100, 100)
    icon = clockwisePic
  }

  val rotateCounterclockwise = new Button {
    preferredSize = new Dimension(100, 100)
    icon = counterclockwisePic
  }

  val playButton = new Button {
    preferredSize = new Dimension(250, 50)
    text = "Play"
    font = defaultFont
  }

  val scoreButton = new Button {
    preferredSize = new Dimension(250, 50)
    text = "Hi-Scores"
    font = defaultFont
  }

  val menuButton = new Button {
    text = "Menu"
    font = defaultFont
  }

  val quitButton = new Button {
    preferredSize = new Dimension(250, 50)
    text = "Quit"
    font = defaultFont
  }
  
  val infoButton = new Button {
    preferredSize = new Dimension(250,50)
    text = "Help"
    font = defaultFont
  }
  
  val backButton = new Button {
    preferredSize = new Dimension(250,50)
    text = "Back"
    font = defaultFont
  }

  
  //The main game screen, lots of insets to align components
  val gameScreen = new Screen {
    focusable = true
    c.gridx = 0
    c.gridy = 0
    c.gridwidth = 6
    c.insets = new Insets(0, 0, 25, 0)
    layout(scoreBoard) = c
    c.gridx = 0
    c.gridy = 1
    c.gridwidth = 3
    c.gridheight = 3
    c.insets = new Insets(0, 0, 0, 25)
    layout(grid) = c
    c.gridwidth = 1
    c.gridheight = 1
    c.gridx = 3
    c.gridy = 1
    c.insets = new Insets(0, 100, 0, 0)
    layout(flipHorizontally) = c
    c.gridx = 5
    c.gridy = 1
    c.insets = new Insets(0, -100, 0, 0)
    layout(flipVertically) = c
    c.gridx = 3
    c.gridy = 2
    c.insets = new Insets(0, 0, 0, 0)
    layout(rotateCounterclockwise) = c
    c.gridx = 5
    c.gridy = 2
    c.insets = new Insets(0, 0, 0, 0)
    layout(rotateClockwise) = c
    c.gridx = 4
    c.gridy = 2
    c.insets = new Insets(25, -25, 0, 25)
    layout(currentPentamino) = c
    c.gridx = 4
    c.gridy = 3
    c.insets = new Insets(25, -50, 0, 0)
    layout(nextPentamino) = c
  }

  //The main menu screen
  val menuScreen = new Screen {
    c.insets = new Insets(13, 0, 13, 0)
    layout(playButton) = c
    c.gridy = 2
    layout(scoreButton) = c
    c.gridy = 4
    layout(infoButton) = c
    c.gridy = 6
    layout(quitButton) = c
  }

  
  //Highscore screen, labels under each other
  val highscoreScreen = new Screen {
    val scoreInfo = new Label { text = "Name, Score, Level, Rows"; font = defaultFont; foreground = Color.WHITE }
    c.gridx = 0
    c.gridy = 0
    c.ipady = 25
    layout(scoreInfo) = c
    c.gridy += 1
    for (score <- highscores) {
      layout(score) = c
      c.gridy += 1
    }
    layout(menuButton) = c
  }
  
  //Instruction screen, instructions as string and a demonstration of creating a row
  val infoScreen = new Screen {
    val instructions = new TextArea(5,30) {
      text = "-The goal of the game is to create as many rows of at least 4 of the same color as possible.\n " +
             "-Longers rows give more points, but are harder to make. \n" +
             "-A row clears all pentaminoes that have blocks in it. \n" +
             "-You can rotate and flip the pentaminoes with the buttons on screen, the mouse wheel or the WASD keys.\n " +
             "-You can move the pentaminoes with your mouse or the arrow keys.\n " +
             "-You can set music on/off with 'm' and soundeffects with 'n'."
      wordWrap = true
      lineWrap = true
      font = defaultFont
      editable = false
      opaque = false
      foreground = Color.WHITE
    }
    val infoGridData = new Grid
    infoGridData.add(Pentamino('f',2,3,3,1,1).flipVertical(), 2, 2)
    infoGridData.add(Pentamino('p',1,1,2,2,1).flipVertical().rotateClockwise(), 4, 3)
    val infoGrid0 = new Display(7,7,infoGridData.colors,infoGridData.edges,30)
    infoGridData.add(Pentamino('l',3,3,2,2,3).rotateCounterClockwise(), 3,5)
    val infoGrid1 = new Display(7,7,infoGridData.colors,infoGridData.edges,30)
    infoGridData.remove(2,2)
    infoGridData.remove(3,5)
    val infoGrid2 = new Display(7,7,infoGridData.colors, infoGridData.edges, 30)
    c.gridwidth = 5
    c.gridx = 0
    c.gridy = 0
    layout(instructions) = c
    c.gridwidth = 1
    c.gridy += 1
    c.gridx = 1
    c.insets = new Insets(30,60,0,0)
    layout(infoGrid0) = c
    c.gridx = 2
    layout(infoGrid1) = c
    c.gridx = 3
    layout(infoGrid2) = c
    c.gridy += 1
    c.gridx = 2
    layout(backButton) = c
    
  }

  //Resets the game and display and displays the gameScreen
  def newGame = {
    Game.newGame
    endGame.enabled = true
    mousePosx = 3
    mousePosy = 3
    updateLabels()
    updateGrids()
    frame.contents = gameScreen
    frame.repaint()
  }

  //Menu items to start a game and end the current game
  val startGame = new MenuItem(Action("New game")(newGame))

  val endGame = new MenuItem(Action("End game")(gameOver)) { enabled = false }

  
  //Called when the game ends, checks if your score is high enough to get on the highscores, if so asks for a name.
  def gameOver: Unit = {
    if (Highscore.isScoreEnough(Game.score, Game.level, Game.rows)) {
      val popup = Dialog.showInput(gameScreen, "Your score is eligible for the Highscore list!", "Highscore!", Dialog.Message.Info, initial = "Insert name")
      val name = popup.getOrElse("Anonymous").replace(' ', '_')
      val newRank = Highscore.setNewScore(name, Game.score, Game.level, Game.rows)
      frame.contents = highscoreScreen
      updateHighscores()
      highscores(newRank).foreground = Color.RED
      Game.newGame
    } else {
      val popup = Dialog.showConfirmation(gameScreen, "Game over! Do you want to play again?", "Game over", Dialog.Options.YesNo)
      if (popup == Dialog.Result.No) {
        frame.contents = menuScreen
        GameSounds.stopMusic()
      }
      else newGame
    }
    endGame.enabled = frame.contents(0) == gameScreen
    frame.repaint()
  }

  //The MainFrame of the program
  val frame: MainFrame = new MainFrame {

    title = "Pentaminoes"
    resizable = false
    location = new Point(200, 50)
    preferredSize = windowSize

    menuBar = new MenuBar {
      contents += new Menu("Game") {
        contents += startGame
        contents += endGame
      }
    }
    contents = menuScreen
    menuScreen.requestFocus

    listenTo(grid.mouse.clicks, grid.mouse.moves, grid.mouse.wheel)
    listenTo(gameScreen.mouse.moves, gameScreen.keys)
    listenTo(flipHorizontally, flipVertically, rotateClockwise, rotateCounterclockwise)
    listenTo(playButton, scoreButton, menuButton, infoButton, backButton, quitButton)
    reactions += {
      //Place the next pentamino at the mouse position
      case MouseClicked(_, point, _, _, _) => {
        Game.placePentamino(point.x / blockSize, point.y / blockSize)
        updateGrids()
        showHypo()
        updateLabels()
        frame.repaint()
        if (!Game.gameOn) gameOver
      }
      //Show the next move at the current mouse position
      case MouseMoved(component, point, _) => {
        if (component == grid) {
          mousePosx = point.x / blockSize
          mousePosy = point.y / blockSize
          showHypo()
        } else updateGrids()
        frame.repaint()
        gameScreen.requestFocus
      }
      //Reactions of all the buttons in the displays
      case ButtonClicked(source) => {
        if (source == flipHorizontally)            Game.currentPentamino.flipHorizontal()
        else if (source == flipVertically)         Game.currentPentamino.flipVertical()
        else if (source == rotateClockwise)        Game.currentPentamino.rotateClockwise()
        else if (source == rotateCounterclockwise) Game.currentPentamino.rotateCounterClockwise()
        else if (source == playButton) newGame
        else if (source == scoreButton) { this.contents = highscoreScreen; updateHighscores() }
        else if (source == menuButton) this.contents = menuScreen
        else if (source == infoButton) this.contents = infoScreen
        else if (source == backButton) this.contents = menuScreen
        else if (source == quitButton) dispose()
        endGame.enabled = this.contents(0) == gameScreen
        updateGrids()
        frame.repaint()
        gameScreen.requestFocus
      }
      //Game can be controlled with keys, move and rotate pentaminoes and mute/enable sounds.
      case KeyPressed(_, key, _, _) => {
        if (key == Key.A)      Game.currentPentamino.rotateCounterClockwise()
        else if (key == Key.D) Game.currentPentamino.rotateClockwise()
        else if (key == Key.W) Game.currentPentamino.flipVertical()
        else if (key == Key.S) Game.currentPentamino.flipHorizontal()
        else if (key == Key.Up)    mousePosy = Math.max(mousePosy - 1, 0)
        else if (key == Key.Down)  mousePosy = Math.min(mousePosy + 1, gridSize - 1)
        else if (key == Key.Right) mousePosx = Math.min(mousePosx + 1, gridSize - 1)
        else if (key == Key.Left)  mousePosx = Math.max(mousePosx - 1, 0)
        else if (key == Key.M) GameSounds.muteMusic()
        else if (key == Key.N) GameSounds.muteEffects()
        else if (key == Key.Enter) {
          Game.placePentamino(mousePosx, mousePosy)
          mousePosx = 3
          mousePosy = 3
        }
        updateGrids()
        showHypo()
        updateLabels()
        frame.repaint()
        if (!Game.gameOn) gameOver
      }
      //Rotate pentaminoes with the mouse wheel
      case MouseWheelMoved(_,_,_,rot) => {
        if (rot > 0) Game.currentPentamino.rotateClockwise()
        else if (rot < 0) Game.currentPentamino.rotateCounterClockwise()
        updateGrids()
        showHypo()
        frame.repaint()
      }
    }
  }
  
  //This is called when the program is run.
  def top: MainFrame = frame
}
  
  
  
  
  