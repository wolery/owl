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
//*                                                                     0-0
//*                                                                   (| v |)
//**********************************************************************w*w***

package com.wolery
package owl

//****************************************************************************

import com.wolery.owl.temporal._

//****************************************************************************

trait Transport
{
  case class Range(from: Tick,to: Tick)

  def play()                      : Unit
  def stop()                      : Unit
  def playing                     : Bool

  def end                         : Tick
  def range                       : Range

  def cursor                      : Tick
  def cursor_=(tick: Tick)        : Unit

  def cycleRange                  : Range
  def cycleRange_=(range: Range)  : Unit

  def punchRange                  : Range
  def punchRange_=(range: Range)  : Unit

  def meter(tick: Tick = cursor)  : Meter
//def scale(tick: Tick = cursor)  : Scale
  def tempo(tick: Tick = cursor)  : Tempo

  def tempo                       : Tempo
  def tempo_*=(ratio: ℝ)          : Unit
  def tempo_= (tempo: Tempo)      : Unit

  def cycling                     : Bool
  def cycling_=(on: Bool)         : Unit

  implicit
  def context                     : Context
}

//****************************************************************************
