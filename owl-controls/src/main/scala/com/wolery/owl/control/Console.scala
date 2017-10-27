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

//****************************************************************************

import java.io.Writer

import javafx.beans.property._//{ObjectProperty,SimpleObjectProperty}
import javafx.event.{ActionEvent,EventHandler}
import javafx.scene.control.TextArea
import javafx.scene.input.KeyEvent

import util._

/**
 *  @author Jonathon Bell
 */
class Console extends TextArea with Logging
{
  private var m_home: ℕ       = 0
  private var m_save: ℕ       = 0
  val m_hist: History = new History

  addEventHandler(KeyEvent.KEY_PRESSED,onKeyPressedHandler(_))
  addEventFilter (KeyEvent.KEY_TYPED,  onKeyTypedFilter   (_))
  addEventHandler(KeyEvent.KEY_TYPED,  onKeyTypedHandler  (_))

  type Handler = EventHandler[ActionEvent]

  val onNewlineProperty:ObjectProperty[Handler] = new SimpleObjectProperty(this,"onNewline")
  def getOnNewline                              = onNewlineProperty.get
  def setOnNewline(h: Handler)                  = onNewlineProperty.set(h)

  val onCompleteProperty:ObjectProperty[Handler]= new SimpleObjectProperty(this,"onComplete")
  def getOnComplete                             = onCompleteProperty.get
  def setOnComplete(h: Handler)                 = onCompleteProperty.set(h)

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

  def onKeyTypedHandler(e: KeyEvent): Unit =
  {
    log.debug("onKeyTypedHandler({})",e)

    if (getModifiers(e)=='_ && e.getCharacter=="\r")
    {
      val s = buffer.trim
      if (s.nonEmpty)
        m_hist.add(buffer)
      getOnNewline.handle(new ActionEvent)
      m_home = getLength
      m_save = m_home
    }

    assert(isConsistent)
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
      case ('⌥,F)           ⇒ endOfNextWord()            // And ⌥RIGHT on OS X
      case ('⌥,B)           ⇒ previousWord()             // And ⌥LEFT  on OS X
      case ('^,X)           ⇒ toggleHome()

   // Editing:

      case ('^,L)           ⇒ notYetImplemented("^L")    // clear screen

      case ('⌥,BACK_SPACE)  ⇒ deletePreviousWord()
      case ('⌥,D)           ⇒ deleteNextWord()
      case ('^,D)           ⇒ deleteNextChar()
      case ('^,H)           ⇒ deletePreviousChar()
      case ('^,W)           ⇒ cutPreviousWord()
      case ('^,K)           ⇒ cutToEnd()
      case ('^,U)           ⇒ cutToHome()
      case ('⌥,T)           ⇒ notYetImplemented("⌥T")    // swapWord()
      case ('^,T)           ⇒ swapChars()
      case ('^,Y)           ⇒ paste()
      case ('⌥,U)           ⇒ upperWord()                // unreachable on OSX
      case ('⌥,L)           ⇒ lowerWord()
      case ('⌥,C)           ⇒ upperChar()
      case ('⌥,R)           ⇒ cancelEdit()
      case ('_,TAB)         ⇒ complete()

   // Command History:

      case ('_,UP)          ⇒ previousHistory()
      case ('_,DOWN)        ⇒ nextHistory()
      case ('^,R)           ⇒ notYetImplemented("^R")//reverseSearchHostory
      case ('^,P)           ⇒ previousHistory()
      case ('^,N)           ⇒ nextHistory()
      case ('^,S)           ⇒ notYetImplemented("^S")//go back to next most recent comand
      case ('^,O)           ⇒ notYetImplemented("^O")//execute command found by ^r or ^s
      case ('^,G)           ⇒ notYetImplemented("^G")//escape history  searching mode
      case ('⌥,PERIOD)      ⇒ notYetImplemented("⌥.")//last argument of previous command

   // Disabled:
      case ('◆,Z)           ⇒ notYetImplemented("◆,Z")//undo
      case ('⇧◆,Z)          ⇒ notYetImplemented("⇧◆,Z")//redo

   // Anything Else...

      case  _               ⇒ consumed = false
    }

    if (consumed)
    {
      e.consume()
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

  def complete(): Unit =
  {
    log.debug("onComplete()")

    getOnComplete.handle(new ActionEvent)
  }

  def previousHistory(): Unit =
  {
    log.debug("previousHistory()")

    buffer = m_hist.previous
  }

  def nextHistory(): Unit =
  {
    log.debug("nextHistory()")

    buffer = m_hist.next
  }

  private
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
  def notYetImplemented(s: String): Unit =
  {
    log.warn(s)
    beep()
  }

  private
  def getModifiers(e: KeyEvent): Symbol =
  {//⇧^⌥◆
    var                   s  = ""
    if (e.isShiftDown)    s += '⇧'
    if (e.isControlDown)  s += '^'
    if (e.isAltDown)      s += '⌥'
    if (e.isMetaDown)     s += '◆'

    if (s.isEmpty()) '_ else Symbol(s)
  }

  private
  def getDebugString(e: KeyEvent): String =
  {
    (getModifiers(e).toString + e.getCode).substring(1)
  }

  private
  def isConsistent(): Bool =
  {
    //assert(isBetween(0,m_home,getLength))
    //assert(isBetween(m_home,m_save,getLength))
    true
  }
}

//****************************************************************************

class History (size: ℕ = 4) extends Logging
{
  require(size > 0)

  var m_buf = collection.mutable.Buffer.fill(size)("")
  var m_nxt: ℕ = 0
  var m_iter: ℕ = 0

  def add(command: String): Unit =
  {
    log.debug("add({})",command.trim)

    assert(command.trim.nonEmpty)

    m_buf(m_nxt % size) = command.trim
    m_iter = m_nxt
    m_nxt += 1
  }

  def get(index: ℕ): String  = m_buf(index % m_buf.size)

  def history(): String =
  {
    val s = new StringBuffer

    for (i ← range)
    {
      s.append(s"   $i  ${get(i)}\n")
    }

    s.toString
  }

  def search(string: String): String = {???}

  def next()                : String =
  {
    if (m_iter + 1 < max)
    {
      m_iter +=1
    }
    else
    {
      beep()
    }
    get(m_iter)
  }

  def previous()            : String =
  {
    val s= get(m_iter)
    
    if (min <= m_iter -1)
    {
      m_iter -=1
    }
    else
    {
      beep()
    }
    s    
  }

  def load(path: String): Unit = {}
  def save(path: String): Unit = {}

  def lo: ℕ = if (get(m_nxt).isEmpty) 0 else m_nxt - m_buf.size
  def hi: ℕ = m_nxt
  def min: ℕ = if (get(m_nxt).isEmpty) 0 else m_nxt - m_buf.size
  def max: ℕ = m_nxt
  def range = lo until hi

  private
  def consistent: Bool =
  {
    assert(m_buf.size == size)
    assert(isBetween(m_nxt,0,size-1))
    return true
  }
}

//****************************************************************************
