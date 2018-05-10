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
package temporal

case class Bar (bar: ℕ = 0,beat: ℕ = 0,division: ℕ = 0,tick: ℕ = 0)
{
  def asTick(implicit m: Context): Tick = m.tick(this)
  def asTime(implicit m: Context): Time = m.time(this)
}

case class Tick(tick: Long) extends AnyVal
{
  def asBar (implicit m: Context): Bar  = m.bar (this)
  def asTime(implicit m: Context): Time = m.time(this)
}

case class Time(millisecond: Long) extends AnyVal
{
  def asBar (implicit m: Context): Bar  = m.bar_(this)
  def asTick(implicit m: Context): Tick = m.tick(this)

  def hour    : ℕ = ???
  def minute  : ℕ = ???
  def second  : ℕ = ???
  def frame   : ℕ = ???
  def subframe: ℕ = ???
}

case class Meter(beats: ℕ = 4,symbol: ℕ = 4,clocks: ℕ = 24,subbeats: ℕ = 8)

//case class Tempo(bpm: ℕ) extends AnyVal

trait Context
{
  def bar  (t: Tick)              : Bar
  def bar_ (t: Time)              : Bar
  def time (t: Bar)               : Time
  def time (t: Tick)              : Time
  def tick (t: Bar)               : Tick
  def tick (t: Time)              : Tick
}

//****************************************************************************
