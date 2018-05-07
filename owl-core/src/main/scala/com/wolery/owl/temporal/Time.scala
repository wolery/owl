//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : Header:
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
  def asTick(implicit m: Map): Tick = m.tick(this)
  def asTime(implicit m: Map): Time = m.time(this)
}

case class Tick(tick: Long) extends AnyVal
{
  def asBar (implicit m: Map): Bar  = m.bar (this)
  def asTime(implicit m: Map): Time = m.time(this)
}

case class Time(millisecond: Long) extends AnyVal
{
  def asBar (implicit m: Map): Bar  = m.bar_(this)
  def asTick(implicit m: Map): Tick = m.tick(this)

  def hour    : ℕ = ???
  def minute  : ℕ = ???
  def second  : ℕ = ???
  def frame   : ℕ = ???
  def subframe: ℕ = ???
}

case class Meter(beats: ℕ = 4,symbol: ℕ = 4,clocks: ℕ = 24,subbeats: ℕ = 8)

case class Tempo(bpm: ℕ) extends AnyVal
{
//def name: String
//Larghissimo — very, very slow (20 bpm and below)
//Grave — slow and solemn (20–40 bpm)
//Lento — slowly (40–60 bpm)
//Largo — broadly (40–60 bpm)
//Larghetto — rather broadly (60–66 bpm)
//Adagio — slow and stately (literally, "at ease") (66–76 bpm)
//Adagietto — rather slow (70–80 bpm)
//Andante moderato — a bit slower than andante
//Andante — at a walking pace (76–108 bpm)
//Andantino – slightly faster than andante
//Moderato — moderately (108–120 bpm)
//Allegretto — moderately fast (but less so than allegro)
//Allegro moderato — moderately quick (112–124 bpm)
//Allegro — fast, quickly and bright (120–168 bpm)
//Vivace — lively and fast (≈140 bpm) (quicker than allegro)
//Vivacissimo — very fast and lively
//Allegrissimo — very fast
//Presto — very fast (168–200 bpm)
//Prestissimo — extremely fast (more than 200bpm)
}

//case class Ratio(ratio: ℝ) extends AnyVal

trait Map
{
  def bar  (t: Tick)              : Bar
  def bar_ (t: Time)              : Bar
  def time (t: Bar)               : Time
  def time (t: Tick)              : Time
  def tick (t: Bar)               : Tick
  def tick (t: Time)              : Tick
}

//****************************************************************************
