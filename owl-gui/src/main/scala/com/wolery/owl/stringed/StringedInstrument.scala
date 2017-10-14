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

//****************************************************************************

import Math.{max,min}

import com.wolery.owl._
import com.wolery.owl.core._
import com.wolery.owl.core.utilities._
import com.wolery.owl.Instrument
import com.wolery.owl.utils.load

import javafx.scene.Parent

//****************************************************************************

case class StringedInstrument(strings: Seq[Pitch],frets: ℕ) extends Instrument
{
  val indices = strings.size * frets

  case class Stop(index: ℕ)
  {
    assert(index < indices)

    def this(string: ℕ,fret: ℕ) =
    {
      this(string * frets + fret)
    }

    def pitch : Pitch = strings(string) + fret
    def string: ℕ     = index / frets
    def fret  : ℕ     = index % frets
    def row   : ℕ     = string
    def col   : ℕ     = fret

    override
    def toString: String = s"${string}${subscript(fret.toString)} $index"
  }

  def lowest: Pitch =
  {
    strings.last
  }

  def highest: Pitch =
  {
    strings(0) + frets
  }

  def stops: Seq[Stop] =
  {
    for (i ← 0 until indices) yield
    {
      new Stop(i)
    }
  }

  def stops(pitch: Pitch): Seq[Stop] =
  {
    for (s ← 0 until strings.size if isBetween(pitch,strings(s),strings(s)+frets-1)) yield
    {
      new Stop(s,pitch-strings(s))
    }
  }

  def view(fxml: String): (Pane,StringedController) =
  {
    load.view(fxml,new StringedController(this))
  }
}

//****************************************************************************

object StringedInstrument
{
  def apply(frets: ℕ,strings: Pitch*): StringedInstrument =
  {
    assert(!strings.isEmpty)

    new StringedInstrument(strings.reverse,1 + frets)
  }
}

//****************************************************************************
