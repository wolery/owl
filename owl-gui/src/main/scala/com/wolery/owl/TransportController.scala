//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose :
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//****************************************************************************

package com.wolery.owl

//****************************************************************************

import Math.max

import com.wolery.owl.core._
import com.wolery.owl.core.utilities._
import com.wolery.owl.midi.messages._
import com.wolery.owl.utils.implicits._

import javafx.animation.{ KeyFrame, Timeline }
import javafx.application.Platform.{ runLater ⇒ defer }
import javafx.css.PseudoClass.getPseudoClass
import javafx.event.ActionEvent
import javafx.fxml.{ FXML ⇒ fx }
import javafx.scene.control.{ Label, Spinner, SpinnerValueFactory }
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory
import javafx.scene.input.MouseEvent
import javafx.util.Duration.millis
import javafx.util.StringConverter
import javax.sound.midi.{ MetaEventListener, MetaMessage }

//****************************************************************************

class TransportController(m_xpt: Transport) extends MetaEventListener
{
  @fx var m_secs:  Label = _
  @fx var m_bars:  Label = _
  @fx var m_prev:  Pane  = _
  @fx var m_rwnd:  Pane  = _
  @fx var m_fwrd:  Pane  = _
  @fx var m_next:  Pane  = _
  @fx var m_rset:  Pane  = _
  @fx var m_stop:  Pane  = _
  @fx var m_play:  Pane  = _
  @fx var m_loop:  Pane  = _
  @fx var m_left:  Label = _
  @fx var m_right: Label = _
  @fx var m_tempo: Spinner[ℝ] = _
  @fx var m_meter: Label = _
  @fx var m_scale: Label = _

      val m_tmr  : Timeline  = newTimer()

  def initialize(): Unit =
  {
    initTempoSpinner()
    updateButtons()
    updateLoopClocks()
  }

  def meta(message: MetaMessage): Unit = message.getType match
  {
    case TEMPO ⇒ defer(onTempoChange())
    case METER ⇒ defer(onMeterChange())
    case SCALE ⇒ defer(onScaleChange())
    case _     ⇒
  }

  def onMeterChange(): Unit =
  {
     m_meter.setText(m_xpt.meter.toString)
  }

  def onTempoChange(): Unit =
  {
    m_tempo.getValueFactory.setValue(m_xpt.tempo)
  }

  def onScaleChange(): Unit =
  {
    m_scale.setText(m_xpt.scale.toString)
  }

  def onTempoSpinChange(was: ℝ,now: ℝ): Unit =
  {
    m_xpt.tempo = now
  }

  def onReset(e: MouseEvent): Unit =
  {
    m_xpt.cursor = 0
  }

  def onStop(e: MouseEvent): Unit =
  {
    m_xpt.stop()
    m_tmr.stop()
    updateButtons()
  }

  def onPlay(e: MouseEvent): Unit =
  {
    m_xpt.play()
    m_tmr.play()
    updateButtons()
  }

  def onLoop(e: MouseEvent): Unit =
  {
    m_xpt.looping = !m_xpt.looping
    updateButtons()
  }

  def onPrevious(e: MouseEvent): Unit =
  {
    m_xpt.cursor = m_xpt.loop.from
    updateClock()
  }

  def onNext(e: MouseEvent): Unit =
  {
    m_xpt.cursor = m_xpt.loop.to
    updateClock()
  }

  var m_tap: Tick = 0
  def onTapTempo(me: MouseEvent): Unit =
  {
//    if (m_xpt.isPlaying)
//    {
//      val t = m_xpt.cursor
//      val d = max(t - m_tap,1) * (if (me.isShiftDown) 2 else 1) * (if (me.isShiftDown)   2 else 1)
//      val e = m_xpt.tempo * m_xpt.ticksPerBeat / d
//
//      if (isBetween(e,MIN_TEMPO,MAX_TEMPO))
//      {
//        m_xpt.tempo = e
//        m_tempo.getValueFactory.setValue(e)
//      }
//
//      m_tap = t
//    }
  }

  def onRewind   (e: MouseEvent): Unit = println("rewind")
  def onForward  (e: MouseEvent): Unit = println("advance")

////
  def updateLoopClocks() =
  {
    updateLeftClock()
    updateRightClock()
  }

  def updateLeftClock(): Unit =
  {
    m_left.setText(m_xpt.measure(m_xpt.loop.from).toString)
  }

  def updateRightClock(): Unit =
  {
    m_right.setText(m_xpt.measure(m_xpt.loop.to).toString)
  }

  def updateClock(): Unit =
  {
    val tpb   = m_xpt.ticksPerBeat
    val tick  = m_xpt.cursor
    val beat  = tick / tpb
    val bars  = 1 + beat / m_xpt.meter.beats
    val beats = 1 + beat % m_xpt.meter.beats
    val parts = 1 + Math.floor(((tick % tpb).toFloat / tpb) * 4).toInt % 4

    m_bars.setText(f"$bars%04d.$beats%02d.$parts%02d")

    val c = m_xpt.cursor
    val m = m_xpt.measure(c)
    println(s"$c : $m")
  }

  def updateButtons(): Unit =
  {
    m_stop.pseudoClassStateChanged(STOPPED,!m_xpt.isPlaying)
    m_play.pseudoClassStateChanged(PLAYING, m_xpt.isPlaying)
    m_loop.pseudoClassStateChanged(LOOPING, m_xpt.isLooping)
  }

////
  def newTimer(): Timeline =
  {
    val t = new Timeline(new KeyFrame(millis(1000),(a:ActionEvent) ⇒ updateClock()))
    t.setCycleCount(-1)
    t
  }

  def initTempoSpinner() =
  {
    val f = new DoubleSpinnerValueFactory(MIN_TEMPO,MAX_TEMPO,m_xpt.tempo)
    {
      val c = getConverter

      setConverter(new StringConverter[java.lang.Double]
      {
        def toString  (r: java.lang.Double): String = f"$r%.2f"
        def fromString(s: String): java.lang.Double = c.fromString(s)
      })
    }

    m_tempo.setValueFactory(f.asInstanceOf[SpinnerValueFactory[ℝ]])
    m_tempo.valueProperty.addListener(onTempoSpinChange _)
  }

  val MIN_TEMPO =   1.0F
  val MAX_TEMPO = 300.0F
  val STOPPED   = getPseudoClass("stopped")
  val PLAYING   = getPseudoClass("playing")
  val LOOPING   = getPseudoClass("looping")
}

//****************************************************************************
