//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : A command line console control implemented as TextArea.
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

import scala.beans.BeanProperty

import javafx.event.{ActionEvent,EventHandler}
import javafx.scene.control.TextArea
import javafx.scene.input.KeyEvent

import util._

/**
 * A command line console control implemented as TextArea.
 *
 * @author Jonathon Bell
 */
class Console extends TextArea with Logging
{
  @BeanProperty var historySize: ℕ                         = 4
  @BeanProperty var onNewline  : EventHandler[ActionEvent] = _
  @BeanProperty var onComplete : EventHandler[ActionEvent] = _
  private       var m_home     : ℕ = 0
  private       var m_save     : ℕ = 0
  private       var m_next     : ℕ = 0
  private       var m_iter     : ℕ = 0
  private lazy  val m_buff = collection.mutable.Buffer.fill(historySize)("")

  addEventHandler(KeyEvent.KEY_PRESSED,onKeyPressedHandler(_))
  addEventFilter (KeyEvent.KEY_TYPED,  onKeyTypedFilter   (_))

  def buffer: String =
  {
    getText.substring(m_home)
  }

  def buffer_=(text: String): Unit =
  {
    replaceText(m_home,getLength,text)
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

  override
  def replaceText(start: ℕ,end: ℕ,text: String) =
  {
    log.debug("replaceText({})",text)

    if (start >= m_home)
    {
      super.replaceText(start,end,text)
    }
  }

  override
  def appendText(text: String) =
  {
    log.debug("appendText({})",text)

    super.appendText(text)
    m_home = getLength
    m_save = m_home
  }

  override
  def selectRange(anchor: ℕ,caret: ℕ): Unit =
  {
    log.debug("selectRange({},{})",anchor,caret)

    val n = getLength
    val a = clamp(m_home,anchor,n)
    val c = clamp(m_home,caret, n)

    super.selectRange(a,c)
  }

  def onKeyTypedFilter(e: KeyEvent): Unit =
  {
    log.debug("onKeyTypedFilter({})",e)

    if (e.isAltDown)
    {
      e.consume()
    }
  }

  def onKeyPressedHandler(e: KeyEvent): Unit =
  {
    log.debug("onKeyPressedHandler({})",e)

    import javafx.scene.input.KeyCode._

    var consumed = true;

    (getModifiers(e),e.getCode) match
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
      case ('⌥,T)           ⇒ unimplemented(e)           // swapWord()
      case ('^,T)           ⇒ swapChars()
      case ('^,Y)           ⇒ paste()
      case ('⌥,U)           ⇒ upperWord()                // unreachable on OSX
      case ('⌥,L)           ⇒ lowerWord()
      case ('⌥,C)           ⇒ upperChar()
      case ('⌥,R)           ⇒ cancelEdit()

   // Command History:

      case ('_,UP)          ⇒ previousHistory()
      case ('_,DOWN)        ⇒ nextHistory()
      case ('^,R)           ⇒ unimplemented(e)           // reverseSearchHostory
      case ('^,P)           ⇒ previousHistory()
      case ('^,N)           ⇒ nextHistory()
      case ('^,S)           ⇒ unimplemented(e)           // go back to next most recent comand
      case ('^,O)           ⇒ unimplemented(e)           // execute command found by ^r or ^s
      case ('^,G)           ⇒ unimplemented(e)           // escape history  searching mode
      case ('⌥,PERIOD)      ⇒ unimplemented(e)           // last argument of previous command

   // Events:

      case ('_,ENTER)       ⇒ newline()
      case ('_,TAB)         ⇒ complete()

   // Disabled:

      case ('◆,Z)           ⇒ unimplemented(e)           // undo
      case ('⇧◆,Z)          ⇒ unimplemented(e)           // redo
      case ('^,L)           ⇒ unimplemented(e)           // clear screen

   // Anything Else...

      case  _               ⇒ consumed = false
    }

    if (consumed)
    {
      e.consume()
    }
  }

  def newline(): Unit =
  {
    log.debug("onEnter({})")

    super.appendText(System.lineSeparator)

    if (onNewline != null)
    {
      onNewline.handle(new ActionEvent)
    }

    m_home = getLength
    m_save = m_home
  }

  def complete(): Unit =
  {
    log.debug("onComplete()")

    if (onComplete != null)
    {
      onComplete.handle(new ActionEvent)
    }
  }

  override
  def home(): Unit =
  {
    log.debug("home()")

    m_save = m_home

    positionCaret(m_home)
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

    when (m_home < getCaretPosition)
    {
      super.backward()
    }
  }

  def toggleHome(): Unit =
  {
    log.debug("toggleHome({})",m_save)

    val c = getCaretPosition

    if (m_home < c)
    {
      m_save = c
      positionCaret(m_home)
    }
    else
    {
      positionCaret(m_save)
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

    when (m_home > getCaretPosition)
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

    selectRange(m_home,getCaretPosition)
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

    when (m_home + 2 <= c)
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
    log.debug("clearLine({},{})",m_home,getLength)

    deleteText(m_home,getLength)
    m_save = m_home
  }

  def addHistory(command: String): Unit =
  {
    m_buff(m_next % m_buff.size) = command
    m_next += 1
    m_iter  = m_next
  }

  def previousHistory(): Unit =
  {
    when (lo <= m_iter -1)
    {
      m_iter -=1
    }

    buffer = get(m_iter)
  }

  def nextHistory(): Unit =
  {
    when (m_iter < hi)
    {
      m_iter += 1
    }

    buffer = if (m_iter < hi) get(m_iter) else ""
  }

  def showHistory(writer: Writer = this.writer): Unit =
  {
    log.debug("showHistory({})")

    for (i ← lo until hi)
    {
      writer.append(f"${i+1}%5d  ${get(i)}%s\n")
    }
  }

  def loadHistory(path: String): Unit =
  {}

  def saveHistory(path: String): Unit =
  {}

  protected
  def get(index: ℕ): String =
  {
    m_buff(index % m_buff.size)
  }

  protected
  def hi: ℕ =
  {
    m_next
  }

  protected
  def lo: ℕ =
  {
    if (get(m_next).isEmpty)
      0
    else
      m_next - m_buff.size
  }

  protected
  def getModifiers(e: KeyEvent): Symbol =
  {//⇧^⌥◆
    var                   s  = ""
    if (e.isShiftDown)    s += '⇧'
    if (e.isControlDown)  s += '^'
    if (e.isAltDown)      s += '⌥'
    if (e.isMetaDown)     s += '◆'

    if (s.isEmpty()) '_ else Symbol(s)
  }

  protected
  def unimplemented(e: KeyEvent): Unit =
  {
    log.debug("unimplemented({}{})",getModifiers(e),e.getCode,"")

    beep()
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

  protected
  def isConsistent(): Bool =
  {
    assert(isBetween(0,m_home,getLength))
    assert(isBetween(m_home,m_save,getLength))
    true
  }
}

//****************************************************************************
