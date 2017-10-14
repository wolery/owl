//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : Header:
//*
//*
//*  Purpose : $Header:$
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//****************************************************************************

package com.wolery.owl
package control

import java.io.Writer
import com.wolery.owl.utils.implicits.asRunnable
import javafx.beans.property.{ ObjectProperty, SimpleObjectProperty }
import javafx.event.{ ActionEvent, EventHandler }
import javafx.scene.control.TextArea
import javafx.scene.input.{KeyCode,KeyEvent}
import com.wolery.owl.utils.utilities._
import javafx.application.Platform.{ runLater ⇒ defer }
import preferences.{eol}
import com.wolery.owl.utils.Logging

//****************************************************************************

class NewlineEvent(val line: String) extends ActionEvent

//****************************************************************************

class Console extends TextArea with Logging
{
  type Handler = EventHandler[NewlineEvent]

  var m_buff: ℕ = 0

  addEventFilter (KeyEvent.KEY_PRESSED,onKeyPressedFilter(_))
  addEventHandler(KeyEvent.KEY_PRESSED,onKeyPressedHandler(_))
  addEventFilter (KeyEvent.KEY_TYPED,  onKeyTypedFilter(_))
  addEventHandler(KeyEvent.KEY_TYPED,  onKeyTypedHandler(_))

  def getOnNewline                  : Handler   = onNewlineProperty.get()
  def setOnNewline(handler: Handler): Unit      = onNewlineProperty.set(handler)
  val onNewlineProperty:ObjectProperty[Handler] = new SimpleObjectProperty(this,"onNewline")

  override
  def replaceText(start: ℕ,end: ℕ,text: String) =
  {
  //log.debug("replaceText({})",text)

    if (start >= m_buff)
    {
      super.replaceText(start,end,text)
    }
  }

  override
  def appendText(text: String) =
  {
  //log.debug("appendText({})",text)

    super.appendText(text)
    m_buff = getLength
  }

  override
  def selectRange(anchor: ℕ,caret: ℕ): Unit =
  {
    log.debug("selectRange({},{})",anchor,caret)

    val n = getLength
    val a = clamp(m_buff,anchor,n)
    val c = clamp(m_buff,caret, n)

    super.selectRange(a,c)
  }

  def onKeyPressedFilter(e: KeyEvent): Unit =
  {
  //log.debug("onKeyPressedFilter({})",e)
  }

  def onKeyPressedHandler(e: KeyEvent): Unit =
  {
  //log.debug("onKeyPressedHandler({})",e)

    (e.getCode,e.getModifiers) match
    {
   // Cursor Movment:
      case (KeyCode.A,      CNTRL)      ⇒ println("^a")
      case (KeyCode.E,      CNTRL)      ⇒ println("^e")
      case (KeyCode.F,      CNTRL)      ⇒ println("^f")
      case (KeyCode.B,      CNTRL)      ⇒ println("^b")

      case (KeyCode.B,      ALT)        ⇒ println("⌥b")
      case (KeyCode.F,      ALT)        ⇒ println("⌥f")

   // Editing:
      case (KeyCode.L,      CNTRL)      ⇒ println("^l")

      case (KeyCode.DELETE, ALT)        ⇒ println("⌥⌫") //
      case (KeyCode.D,      ALT)        ⇒ println("⌥d")

      case (KeyCode.D,      CNTRL)      ⇒ println("^d")
      case (KeyCode.H,      CNTRL)      ⇒ println("^h")

      case (KeyCode.W,      CNTRL)      ⇒ println("^w")
      case (KeyCode.K,      CNTRL)      ⇒ println("^k")
      case (KeyCode.U,      CNTRL)      ⇒ println("^u")

      case (KeyCode.T,      ALT)        ⇒ println("⌥t")
      case (KeyCode.T,      CNTRL)      ⇒ println("^t")
    //case (KeyCode.T,      ESC)        ⇒ println("esc-t")

      case (KeyCode.Y,      CNTRL)      ⇒ println("^y")

      case (KeyCode.U,      ALT)        ⇒ println("⌥u") //ß
      case (KeyCode.L,      ALT)        ⇒ println("⌥l")
      case (KeyCode.C,      ALT)        ⇒ println("⌥c")
      case (KeyCode.R,      ALT)        ⇒ println("⌥r")

      case (KeyCode.MINUS,  SHIFT|CNTRL)⇒ println("⇧^_") //

      case (KeyCode.TAB,    NONE)       ⇒ println("TAB")  //

   // Command History:

      case (KeyCode.UP     ,NONE)       ⇒ println("UP") //
      case (KeyCode.DOWN   ,NONE)       ⇒ println("DN") //

      case (KeyCode.R,      CNTRL)      ⇒ println("^r")
      case (KeyCode.P,      CNTRL)      ⇒ println("^p")
      case (KeyCode.N,      CNTRL)      ⇒ println("^n")
      case (KeyCode.PERIOD, ALT)        ⇒ println("⌥.")
      case (KeyCode.S,      CNTRL)      ⇒ println("^s")
      case (KeyCode.O,      CNTRL)      ⇒ println("^o")
      case (KeyCode.G,      CNTRL)      ⇒ println("^g") //

      case (KeyCode.X,      SHIFT|CNTRL|ALT) ⇒

        println(e)
        println("⇧^⌥x")

      case  _ ⇒ log.debug("onKeyPressedHandler({})",e)
    }
  }

  def onKeyTypedFilter(e: KeyEvent): Unit =
  {
    log.debug("onKeyTypedFilter({})",e)
    
    if (e.getModifiers == ALT)
    {
      e.consume
    }
  }

  def onKeyTypedHandler(e: KeyEvent): Unit =
  {
    log.debug("onKeyTypedHandler({})",e)

    (e.getCharacter,e.getModifiers) match
    {
      case ("\r",NONE) ⇒
      {
        val b = buffer
        m_buff = getLength
        onNewlineProperty.get.handle(new NewlineEvent(b))
      }
//    case ("\t",NONE) ⇒
      case  _ ⇒
    }
  }

  def buffer: String =
  {
    getText.substring(m_buff)
  }

  def buffer_=(text: String): Unit =
  {
    replaceText(m_buff,getLength,text)
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

  val NONE : ℕ = 0
  val SHIFT: ℕ = 1
  val CNTRL: ℕ = 2
  val ALT  : ℕ = 4
//val META : ℕ = 8

  implicit class KeyEventEx(e: KeyEvent)
  {
    def getModifiers: ℕ =
    {
      var                   m  = NONE
      if (e.isShiftDown)    m |= SHIFT
      if (e.isControlDown)  m |= CNTRL
      if (e.isAltDown)      m |= ALT
//    if (e.isMetaDown)     m |= META
      m
    }
  }
}

//****************************************************************************
