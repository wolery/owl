//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : A command line console control implemented as a TextArea.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//*  See Also: https://ss64.com/osx/syntax-bashkeyboard.html
//*                                                                     0-0
//*                                                                   (| v |)
//**********************************************************************w*w***

package com.wolery.owl
package control

import java.io.Writer
import java.lang.System.{lineSeparator ⇒ EOL}
import javafx.event.{ActionEvent,Event,EventHandler,EventType}
import javafx.scene.control.TextArea
import javafx.scene.input.{KeyCode,KeyEvent}
import javafx.scene.input.KeyEvent.{KEY_PRESSED,KEY_TYPED}
import scala.beans.BeanProperty
import scala.collection.mutable.Buffer

import util._

/**
 * An embeddable command line console control implemented as a TextArea.
 *
 * = Overview =
 *
 * Partitions the contents of the underlying text area into 3 disjoint area:
 *  - output area - a read only view of text previously written
 *  - prompt			 -
 *  - input  		 - an editable buffer in which the user prepares the next
 *  								 command. emacs-like key bindings, history mechanism etc.
 *
 * Example:
 *
 * 	Output|PromptInput
 *
 * m_prompt  =  7
 * m_input   = 13
 * getLength = 18
 *
 * = Events =
 *
 *  - Accept					action event fired when command line accepted
 *  - Complete				action event fired when tab completion requested
 *
 * = Bean Properties =
 *
 * 	 Visible in SceneBuilder
 *
 *  - History Size		size of command history buffer > 0
 *  - onAccept				event handler for Input action event
 *  - onComplete			event handler for Complete action event
 *
 * = Properties =
 *
 *  - output					current contents of output region (read only)
 *  - prompt					current contents of prompt region
 *  - input					current contents of input  region
 *  - writer
 *
 * = State =
 *
 *  - m_prompt				start of prompt region
 *  - m_input				start of input  region
 *  - m_toggle				el of input : m_input<=toggle<=etLength
 *
 * 	- m_cursor				points into history buffer
 * 	- m_latest				insertion point for next command in hist buff
 * 	- m_search				holds current history search
 *  - m_history
 *
 * = Invariants =
 *
 *		0 <= m_prompt <= m_input  <= m_toggle <= getLength
 *	  0 <= m_cursor%hsize <= m_latest%hsize
 *
 * @author Jonathon Bell
 */
class Console extends TextArea with Logging
{
  type Action  = EventHandler[ActionEvent]
  type Filters = Seq[(EventType[KeyEvent],EventHandler[KeyEvent])]

  @BeanProperty var onAccept  : Action = _
  @BeanProperty var onComplete: Action = _

  def getHistorySize: ℕ =
  {
    m_history.size
  }

  def setHistorySize(size: ℕ): Unit =
  {
    log.debug("setHistorySize({})",size)

    val min: ℕ = 0
    val max: ℕ = 10
    val δ  : ℤ = size - m_history.size

    if (size<min || max<size)
    {
      log.warn(s"Bad history size $size: should be $min<=size<=$max")

      setHistorySize(clamp(min,size,max))
    }
    else
    if (δ > 0)
    {
      m_history ++= Seq.fill(δ)("")
    }
    else
    if (δ < 0)
    {
      m_history.trimEnd(-δ)
    }

    assert(getHistorySize == size)
  }

//****************************************************************************
//  private var m_outsize : ℕ = 5
//  def getOutputLines: ℕ = 5


//****************************************************************************

  private var m_prompt : ℕ = 0                           // Start of prompt
  private var m_input  : ℕ = 0                           // Start of input
  private var m_toggle : ℕ = 0                           // Saved caret point

  private var m_cursor : ℕ = 0                           // The history cursor
  private var m_latest : ℕ = 0                           //
  private var m_search : Option[Search]  = None          //
  private val m_history: Buffer[String] = Buffer.fill(10)("")

  private val m_filters: Filters = Seq((KEY_PRESSED,onKeyPressed _),
                                       (KEY_TYPED,  onKeyTyped   _))

  swap(Seq(),m_filters)

//****************************************************************************

  def output: String =
  {
    getText(0,m_prompt)
  }

  def prompt: String =
  {
    getText(m_prompt,m_input)
  }

  def prompt_=(string: String): Unit =
  {
    super.replaceText(m_prompt,m_input,string)

    setInputArea(m_prompt + string.length)

    assert(isConsistent)
  }

  def input: String =
  {
    getText(m_input,getLength)
  }

  def input_=(string: String): Unit =
  {
    super.replaceText(m_input,getLength,string)

    assert(isConsistent)
  }

  val writer: Writer = new Writer
  {
    def close: Unit = {}
    def flush: Unit = {}
    def write(array: Array[Char],offset: ℕ,length: ℕ): Unit =
    {
      appendText(new String(array.slice(offset,offset + length)))
    }
  }

//****************************************************************************

  override
  def replaceText(start: ℕ,end: ℕ,text: String) =
  {
    log.debug("replaceText({})",text)

    if (start >= m_input)
    {
      super.replaceText(start,end,text)
    }

    assert(isConsistent)
  }

  override
  def appendText(text: String) =
  {
    log.debug("appendText({})",text)

    super.appendText(text)

    m_prompt = getLength

    setInputArea(getLength)

    assert(isConsistent)
  }

  def appendLine(string: String) =
  {
    log.debug("appendLine({})",string)

    appendText(string + EOL)

    assert(isConsistent)
  }

  override
  def selectRange(anchor: ℕ,caret: ℕ): Unit =
  {
    log.debug("selectRange({},{})",anchor,caret)

    val n = getLength
    val a = clamp(m_input,anchor,n)
    val c = clamp(m_input,caret, n)

    super.selectRange(a,c)
  }

  def onKeyTyped(e: KeyEvent): Unit =
  {
    log.debug("onKeyTyped  ({})",e)

    if (e.isAltDown)
    {
      e.consume()
    }
  }

  def onKeyPressed(e: KeyEvent): Unit =
  {
    log.debug("onKeyPressed({})",e)

    import KeyCode._

    var consumed = true;

    getKeyCombo(e) match
    {
   // Cursor Movement:

      case ('^,A)           ⇒ home()
      case ('^,E)           ⇒ end()
      case ('^,F)           ⇒ forward()
      case ('^,B)           ⇒ backward()
      case ('⌥,F)           ⇒ endOfNextWord()
      case ('⌥,B)           ⇒ previousWord()
      case ('^,X)           ⇒ toggleHome()

   // Editing:

      case ('⌥,BACK_SPACE)  ⇒ deletePreviousWord()
      case ('⌥,D)           ⇒ deleteNextWord()
      case ('^,D)           ⇒ deleteNextChar()
      case ('^,H)           ⇒ deletePreviousChar()
      case ('^,W)           ⇒ cutPreviousWord()
      case ('^,K)           ⇒ cutToEnd()
      case ('^,U)           ⇒ cutToHome()
    //case ('⌥,T)           ⇒ unimplemented(e)           // swapWord()
      case ('^,T)           ⇒ swapChars()
      case ('^,Y)           ⇒ paste()
      case ('⌥,U)           ⇒ upperWord()                // unreachable on OSX
      case ('⌥,L)           ⇒ lowerWord()
      case ('⌥,C)           ⇒ upperChar()
      case ('⌥,R)           ⇒ cancelEdit()

   // Command History:

      case ('^,P)|('_,UP)   ⇒ previousHistory()
      case ('^,N)|('_,DOWN) ⇒ nextHistory()
      case ('^,R)           ⇒ searchHistory()
      case ('⌥,PERIOD)      ⇒ unimplemented(e)           // last argument of previous command

   // Events:

      case ('_,ENTER)       ⇒ accept()
      case ('_,TAB)         ⇒ complete()

   // Disabled:

      case ('◆, Z)          ⇒ unimplemented(e)           // undo
      case ('⇧◆,Z)          ⇒ unimplemented(e)           // redo

   // Anything Else...

      case  _               ⇒ consumed = false
    }

    if (consumed)
    {
      e.consume()
    }
  }

  def accept(): Unit =
  {
    log.debug("accept({})",input)

    super.appendText(EOL)

    if (onAccept != null)
    {
      onAccept.handle(new ActionEvent)
    }

//    setInputArea(getLength)
  }

  def complete(): Unit =
  {
    log.debug("complete({})",input)

    if (onComplete != null)
    {
      onComplete.handle(new ActionEvent)
    }
  }

  override
  def home(): Unit =
  {
    log.debug("home()")

    m_toggle = m_input

    positionCaret(m_input)
  }

  override
  def forward(): Unit =
  {
    log.debug("forward()")

    when (getCaretPosition < getLength)
    {
      super.forward()
    }
  }

  override
  def backward(): Unit =
  {
    log.debug("backward()")

    when (m_input < getCaretPosition)
    {
      super.backward()
    }
  }

  def toggleHome(): Unit =
  {
    log.debug("toggleHome({})",m_toggle)

    val c = getCaretPosition

    if (m_input < c)
    {
      m_toggle = c
      positionCaret(m_input)
    }
    else
    {
      positionCaret(m_toggle)
    }
  }

  def deletePreviousWord(): Unit =
  {
    log.debug("deletePreviousWord()")

    val c = getCaretPosition
    previousWord()
    deleteText(getCaretPosition,c)
  }

  def deleteNextWord(): Unit =
  {
    log.debug("deleteNextWord()")

    val c = getCaretPosition
    endOfNextWord()
    deleteText(c,getCaretPosition)
  }

  override
  def deleteNextChar(): Bool =
  {
    log.debug("deleteNextChar()")

    when (super.deleteNextChar())
    {}
  }

  override
  def deletePreviousChar(): Bool =
  {
    log.debug("deletePreviousChar()")

    when (super.deletePreviousChar())
    {}
  }

  def cutPreviousWord(): Unit =
  {
    log.debug("cutPreviousWord()")

    when (m_input > getCaretPosition)
    {
      selectPreviousWord()
      cut()
    }
  }

  def cutToEnd(): Unit =
  {
    log.debug("cutToEnd()")

    selectRange(getCaretPosition,getLength)
    cut()
  }

  def cutToHome(): Unit =
  {
    log.debug("cutToHome()")

    selectRange(m_input,getCaretPosition)
    cut()
  }

  def swapWord():  Unit =
  {
    log.debug("swapWord()")
  }

  def swapChars(): Unit =
  {
    log.debug("swapChars()")

    var c = getCaretPosition
    val e = getLength

    when (m_input + 2 <= c)
    {
      if (c < e)
      {
        c -= 1
      }
      else
      {
        c -= 2
      }

      val s = getText(c,c+2)
      val t = s"${s(1)}${s(0)}"

      replaceText(c,c+2,t)
    }
  }

  def upperWord(): Unit =
  {
    log.debug("upperWord()")

    val c = getCaretPosition

    if (c < getLength)
    {
      endOfNextWord()
      val e = getCaretPosition
      replaceText(c,e,getText(c,e).toUpperCase)
    }
  }

  def lowerWord(): Unit =
  {
    log.debug("lowerWord()")

    val c = getCaretPosition

    if (c < getLength)
    {
      endOfNextWord()
      val e = getCaretPosition
      replaceText(c,e,getText(c,e).toLowerCase)
    }
  }

  def upperChar(): Unit =
  {
    log.debug("upperChar()")

    var c = getCaretPosition

    if (c < getLength)
    {
      endOfNextWord()
      previousWord()
      c = getCaretPosition
      replaceText(c,c+1,getText(c,c+1).toUpperCase)
      endOfNextWord()
    }
  }

  def clearLine(): Unit =
  {
    log.debug("clearLine({},{})",m_input,getLength)

    deleteText(m_input,getLength)
    m_toggle = m_input
  }

  def getHistory(index: ℕ): String =
  {
    m_history(index % m_history.size)
  }

  def addHistory(command: String): Unit =
  {
    m_history(m_latest % m_history.size) = command
    m_latest += 1
    m_cursor  = m_latest
  }

  def previousHistory(): Unit =
  {
    when (lo <= m_cursor -1)
    {
      m_cursor -=1
    }

    input = getHistory(m_cursor)
  }

  def nextHistory(): Unit =
  {
    when (m_cursor < hi)
    {
      m_cursor += 1
    }

    input = if (m_cursor < hi) getHistory(m_cursor) else ""
  }

  def showHistory(writer: Writer = this.writer): Unit =
  {
    log.debug("showHistory({})")

    for (i ← lo until hi)
    {
      writer.append(f"${i+1}%5d  ${getHistory(i)}%s\n")
    }
  }

  def searchHistory(): Unit =
  {
    log.debug("searchHistory()")

    assert(m_search.isEmpty)

    m_search = Some(new Search)
  }

  protected
  def lo: ℕ =
  {
    if (getHistory(m_latest).isEmpty)
      0
    else
      m_latest - m_history.size
  }

  protected
  def hi: ℕ =
  {
    m_latest
  }

//  protected
//  def getModifiers(e: KeyEvent): Symbol =
//  {//⇧^⌥◆
//    var                   s  = ""
//    if (e.isShiftDown)    s += '⇧'
//    if (e.isControlDown)  s += '^'
//    if (e.isAltDown)      s += '⌥'
//    if (e.isMetaDown)     s += '◆'
//
//    if (s.isEmpty()) '_ else Symbol(s)
//  }

  protected
  def getKeyCombo(e: KeyEvent): (Symbol,KeyCode) =
  {//⇧^⌥◆
    val                   c  = e.getCode
    var                   s  = ""
    if (e.isShiftDown)    s += '⇧'
    if (e.isControlDown)  s += '^'
    if (e.isAltDown)      s += '⌥'
    if (e.isMetaDown)     s += '◆'

    if (s.isEmpty()) ('_,c) else (Symbol(s),c)
  }

  protected
  def unimplemented(e: KeyEvent): Unit =
  {
    log.debug("unimplemented({})",getKeyCombo(e))

    beep()
  }

  private
  def setInputArea(input: ℕ): Unit =
  {
    log.trace("setInputArea({})",input)

    m_input  = input
    m_toggle = input

    assert(isConsistent)
  }

  protected
  def when(condition: Bool)(action: ⇒ Unit): Bool =
  {
    if (condition)
    {
      action
    }
    else
    {
      beep()
    }

    condition
  }

  private
  def swap(was: Filters,now: Filters): Unit = defer
  {
    was.foreach{case (t,h) ⇒ removeEventFilter(t,h)}
    now.foreach{case (t,h) ⇒ addEventFilter   (t,h)}
  }

  /**
   *
   */
  private
  class Search
  {
    val filters : Filters = Seq((KEY_PRESSED,onKeyPressed _ ),
                                (KEY_TYPED,  onKeyTyped   _ ))
    val m_backup = (prompt,input,m_cursor)
    var m_pattern: String = ""
    var m_matches: Seq[String] = Seq()

    swap(m_filters,filters)
    refine(input)

    def cancel(string: String): Unit =
    {
      log.debug("search.cancel({})",string)

      prompt   = m_backup._1
      input    = string
      m_cursor = m_backup._3

      swap(filters,m_filters)
      m_search = None
    }

    def update(string: String = "") : Unit =
    {
      log.debug("search.update({})",string)

      prompt = s"(reverse-i-search)'$m_pattern': "
      input  = string
    }

    def backspace(): Unit =
    {
      log.debug("search.backspace()")

      m_pattern = m_pattern.dropRight(1)

      refine()
    }

   def moveCursor(δ: Int): Unit =
   {
      when (isBetween(m_cursor + δ,0,m_matches.size-1))
      {
        m_cursor += δ
        val m = m_matches(m_cursor)
        update(m)
        positionCaret(m_input + m.indexOfSlice(m_pattern))
      }
    }

    def refine(chars: String = ""): Unit =
    {
      log.debug("search.refine({})",chars)

      m_pattern += chars

      m_matches = m_history.filter(_.contains(m_pattern))

      if (m_matches.nonEmpty)
      {
        m_cursor = m_matches.size - 1
        moveCursor(0)
      }
      else
      {
        update()
      }
    }

    def onKeyPressed(e: KeyEvent): Unit =
    {
      log.debug("onKeyPressed{}",getKeyCombo(e))

      import KeyCode._

      getKeyCombo(e) match
      {
        case ('^,R)                      ⇒ moveCursor(-1)
        case ('^,S)                      ⇒ moveCursor(+1)
        case ('_,BACK_SPACE)             ⇒ backspace()

        case ('^,G)                      ⇒ cancel(m_backup._2)
        case ('_,ENTER)                  ⇒ cancel(input);accept()
        case (_,TAB|UP|DOWN|LEFT|RIGHT)  ⇒ cancel(input)

        case  _                          ⇒
      }

      e.consume()
    }

    def onKeyTyped(e: KeyEvent): Unit =
    {
      log.debug("onKeyTyped  ({})",e.getCharacter)

      val c = e.getCharacter

      if (c.nonEmpty && isPrinting(c(0)))
      {
        refine(c)
      }

      e.consume()
    }
  }

  /**
   *
   */
  @inline private
  def isPrinting(char: Char): Bool =
  {
    isBetween(char,0x20,0x7E)
  }

  /**
   *
   */
  @inline private
  def defer[α](action: ⇒ α): Unit =
  {
    javafx.application.Platform.runLater(() => action)   //
  }

  /**
   * Returns true if the object appears to be in a consistent state.
   *
   * Centralizes a number of consistency checks that otherwise tend to clutter
   * up the code.  Since only ever called from within assertions, these can be
   * eliminated from the release build by the compiler entirely.
   *
   * @return true - always.
   */
  private
  def isConsistent: Bool =
  {
    assert(isIncreasing(0,m_prompt,m_input,m_toggle,getLength))
    true
  }
}

//****************************************************************************
