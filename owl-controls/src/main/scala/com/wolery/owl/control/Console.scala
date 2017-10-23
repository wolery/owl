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
//*  See Also: https://ss64.com/bash/syntax-keyboard.html
//*                                                                     0-0
//*                                                                   (| v |)
//**********************************************************************w*w***

package com.wolery.owl
package control

//****************************************************************************

import java.io.Writer

import javafx.beans.property.{ObjectProperty,SimpleObjectProperty}
import javafx.event.{ActionEvent,EventHandler}
import javafx.scene.control.TextArea
import javafx.scene.input.KeyEvent

import util._

//****************************************************************************

class Console extends TextArea with Logging
{
  private var m_home: ℕ = 0
  private var m_save: ℕ = 0

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

  def onKeyPressedHandler(e: KeyEvent): Unit =
  {
    log.debug("onKeyPressedHandler({})",e)

    import javafx.scene.input.KeyCode._

    var consumed = true;

    (getModifiers(e),e.getCode) match
    {
   // Cursor Movment:

      case ('^,A)           ⇒ home()
      case ('^,E)           ⇒ end()
      case ('^,F)           ⇒ forward()
      case ('^,B)           ⇒ backward()
      case ('⌥,B)           ⇒ previousWord()
      case ('⌥,F)           ⇒ nextWord()
      case ('^,X)           ⇒ toggleHome()

   // Editing:

      case ('^,L)           ⇒ clearLine()
      case ('⌥,BACK_SPACE)  ⇒ deletePreviousWord()
      case ('⌥,D)           ⇒ deleteNextWord()
      case ('^,D)           ⇒ deleteNextChar()
      case ('^,H)           ⇒ deletePreviousChar()
      case ('^,W)           ⇒ cutPreviousWord()
      case ('^,K)           ⇒ cutToEnd()
      case ('^,U)           ⇒ cutFromHome()
      case ('⌥,T)           ⇒ swapPreviousWord()
      case ('^,T)           ⇒ swapChars()
      case ('⇧^,T)          ⇒ swapPreviousWords()
      case ('^,Y)           ⇒ paste()
      case ('⌥,L)           ⇒ lowerWord()
      case ('⌥,C)           ⇒ upperNextChar()
      case ('⌥,R)           ⇒ cancelEdit()
      case ('⇧^,MINUS)      ⇒ undo()
      case ('_,TAB)         ⇒ complete()

   // Command History:

      case ('_,UP)          ⇒ println("UP")
      case ('_,DOWN)        ⇒ println("DN")
      case ('^,R)           ⇒ println("^R")
      case ('^,P)           ⇒ println("^P")
      case ('^,N)           ⇒ println("^N")
      case ('⌥,PERIOD)      ⇒ println("⌥.")
      case ('^,S)           ⇒ println("^S")
      case ('^,O)           ⇒ println("^O")
      case ('^,G)           ⇒ println("^G")

   // Anything Else...

      case  _                ⇒ consumed = false
    }

    if (consumed)
    {
      e.consume()
    }
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
      getOnNewline.handle(new ActionEvent)
      m_home  = getLength
      m_save = m_home
    }

    assert(isConsistent)
  }

  def buffer: String =
  {
    getText.substring(m_home)
  }

  def buffer_=(text: String): Unit =
  {
    replaceText(m_home,getLength,text)
  }

  val writer = new Writer
  {
    def close: Unit = {}
    def flush: Unit = {}
    def write(array: Array[Char],offset: ℕ,length: ℕ): Unit =
    {
      appendText(new String(array.slice(offset,offset + length)))
    }
  }

  def toggleHome(): Unit =
  {
    log.debug("toggleHome({})",m_save)

    val c = getCaretPosition

    if (c > m_home)
    {
      selectRange(m_home,m_home)
      m_save = c
    }
    else
    {
      selectRange(m_save,m_save)
    }
  }

  def clearLine(): Unit =
  {
    log.debug("clearLine({},{})",m_home,getLength)

    deleteText(m_home,getLength)
    m_save = m_home
  }

  def deletePreviousWord(): Unit =
  {
    log.debug("deletePreviousWord()")

    val e = getCaretPosition
    previousWord()
    val s = getCaretPosition
    deleteText(s,e)
  }

  def deleteNextWord(): Unit =
  {
    log.debug("deleteNextWord()")

    val e = getCaretPosition
    nextWord()
    val s = getCaretPosition
    deleteText(s,e)
  }

  def complete(): Unit =
  {
    log.debug("onComplete()")

    getOnComplete.handle(new ActionEvent)
  }

  def cutPreviousWord(): Unit =
  {
    log.debug("cutPreviousWord()")

    selectPreviousWord()
    cut()
  }

  def cutToEnd(): Unit =
  {
    log.debug("cutToEnd()")

    selectRange(getCaretPosition,getLength)
    cut()
  }

  def cutFromHome(): Unit =
  {
    log.debug("cutFromHome()")

    selectRange(m_home,getCaretPosition)
    cut()
  }

  def swapPreviousWord():  Unit = {}
  def swapPreviousWords(): Unit = {}

  def swapChars(): Unit =
  {
    log.debug("swapChars()")

    var c = getCaretPosition
    val e = getLength

    if (m_home + 2 <= c)
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

  def upperNextChar(): Unit =
  {
    log.debug("upperNextChar()")

    val c = getCaretPosition

    if (c < getLength)
    {
      replaceText(c,c+1,getText(c,c+1).toUpperCase)
      endOfNextWord()
    }
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
