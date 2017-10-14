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

package com.wolery.owl.stringed

import com.wolery.owl.Controller
import com.wolery.owl.core.Pitch
import com.wolery.owl.core.Scale
import com.wolery.owl.gui.Bead
import com.wolery.owl.midi.messages._
import com.wolery.owl.utils.implicits._
import com.wolery.owl._

import javafx.application.Platform.{ runLater ⇒ defer }
import javafx.fxml.{ FXML ⇒ fx }
import javafx.scene.Node
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.Pane
import javafx.scene.layout.RowConstraints
import javafx.util.Duration.millis
import javax.sound.midi.MetaMessage
import javax.sound.midi.MidiMessage
import javax.sound.midi.ShortMessage
import javax.sound.midi.ShortMessage.NOTE_OFF
import javax.sound.midi.ShortMessage.NOTE_ON

//****************************************************************************

class StringedController(val instrument: StringedInstrument) extends Controller
{
  @fx
  var root: Pane     = _
  val chan: ℕ        = 0
  val grid: GridPane = newGrid()
  val harm: Beads    = new Beads(0)
  val mldy: Beads    = new Beads(1)

  class Beads(id: ℕ)
  {
    val beads: Seq[Bead] =
    {
      instrument.stops.map(bead(_))
    }

    def apply(stop: instrument.Stop): Bead =
    {
      beads(stop.index)
    }

    private
    def bead(stop: instrument.Stop): Bead =
    {
      val b = id match
      {
        case 0 ⇒ new Bead(stop.pitch.note.toString,"harmonic")
        case 1 ⇒ new Bead(stop.pitch.note.toString,"melodic")
      }

      grid.add(b,stop.col,stop.row)
      b
    }
  }

  def initialize(): Unit =
  {
    root.getChildren.add(grid)
  }

  def view: Pane =
  {
    root
  }

  def send(message: MidiMessage,timestamp: Long): Unit =
  {
    message match
    {
      case m: ShortMessage if m.getChannel==chan ⇒ m.getCommand match
      {
        case NOTE_OFF ⇒ onNoteOff(Pitch(m.getData1))
        case NOTE_ON  ⇒ onNoteOn (Pitch(m.getData1))
        case _        ⇒
      }
      case m: MetaMessage ⇒ m.getType match
      {
        case SCALE ⇒ onScale(m.scale)
        case _     ⇒
      }
      case   _     ⇒
    }
  }

  def onNoteOn(pitch: Pitch) =
  {
    for (stop ← instrument.stops(pitch))
    {
      fade(1.0)(mldy(stop))
    }
  }

  def onNoteOff(pitch: Pitch) =
  {
    for (stop ← instrument.stops(pitch))
    {
      fade(0.0)(mldy(stop))
    }
  }

  def onScale(scale: Scale) =
  {
    for (stop ← instrument.stops if scale.contains(stop.pitch.note))
    {
      fade(1.0)(harm(stop))
    }
  }

  def fade(to: Double,ms: ℕ = 500)(node: Node): Unit =
  {
    val t = new javafx.animation.FadeTransition(millis(ms),node)
    t.setToValue  (to)
    t.play()
  }

  def newGrid(): GridPane =
  {
    def rows: Seq[RowConstraints] =
    {
      val n = instrument.strings.size
      val h = 100.0 / n

      for (r ← 0 until n) yield
      {
        new RowConstraints(){setPercentHeight(h)}
      }
    }

    def cols: Seq[ColumnConstraints] =
    {
      val n = instrument.frets
      val w = 100.0 / n

      for (c ← 0 until n) yield
      {
        new ColumnConstraints(){setPercentWidth(w);setHalignment(javafx.geometry.HPos.CENTER)}
      }
    }
    val g = new GridPane
  //g.gridLinesVisible = true
    g.getRowConstraints.addAll   (rows:_*)
    g.getColumnConstraints.addAll(cols:_*)
    g
  }
}

//****************************************************************************
