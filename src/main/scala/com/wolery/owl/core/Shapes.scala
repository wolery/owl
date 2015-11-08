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

package com.wolery.owl.core

//****************************************************************************

import scala.collection.mutable.Map

//****************************************************************************

private[core]
object Shapes
{
  final class Info (names: String,val shape: Shape)
  {
    def name: Name                        = names.split(":",2).head
    def aliases: Seq[Name]                = names.split(":")
  }

  def apply(b: Bits): Maybe[Info]         = bybits.get(b)
  def apply(n: Name): Maybe[Info]         = byname.get(n.toLowerCase)

  private def f(names: String,intervals: ℤ*) =
  {
    require(names.nonEmpty)

    val shape = Shape(intervals: _*)
    val info  = new Info(names,shape)

    for (name ← names.split(":"))
    {
      bybits += shape.bits       → info
      byname += name.toLowerCase → info
    }
  }

  private val bybits: Map[Bits,Info]      = Map.empty
  private val byname: Map[Name,Info]      = Map.empty

//****************************************************************************
// see https://en.wikipedia.org/wiki/Jazz_scale

  f("chromatic",                                         1,1,1,1,1,1,1,1,1,1,1,1)

// symmetric diminished

  f("whole half",                                        2,1,2,1,2,1,2,1)
  f("half whole",                                        1,2,1,2,1,2,1,2)

// diatonic major

  f("ionian:major:diatonic",                             2,2,1,2,2,2,1)
  f("dorian",                                            2,1,2,2,2,1,2)
  f("phrygian",                                          1,2,2,2,1,2,2)
  f("lydian",                                            2,2,2,1,2,2,1)
  f("myxolydian",                                        2,2,1,2,2,1,2)
  f("aeolian:natural minor",                             2,1,2,2,1,2,2)
  f("locrian",                                           1,2,2,1,2,2,2)

// melodic minor

  f("melodic minor",                                     2,1,2,2,2,2,1)
  f("phrygian ♮6:dorian ♭)",                             1,2,2,2,2,1,2)
  f("lydian augmented:super lydian:lydian ♯5:acoustic",  2,2,2,2,1,2,1)
  f("lydian dominant:lydian ♭7:mixolydian ♯4",           2,2,2,1,2,1,2)
  f("mixolydian ♭6:melodic major",                       2,2,1,2,1,2,2)
  f("locrian ♮2:dorian ♭5:half diminished",              2,1,2,1,2,2,2)
  f("altered dominant:super locrian",                    1,2,1,2,2,2,2)

// harmonic minor
// see http://docs.solfege.org/3.22/C/scales/har.html

  f("harmonic minor",                                    2,1,2,2,1,3,1)
  f("locrian ♯6",                                        1,2,2,1,3,1,2)
  f("ionian augmented:ionian ♯5",                        2,2,1,3,1,2,1)
  f("romanian:dorian ♯4",                                2,1,3,1,2,1,2)
  f("phrygian dominant:phrygian ♯3",                     1,3,1,2,1,2,2)
  f("lydian ♯2",                                         3,1,2,1,2,2,1)
  f("ultra locrian:myxolydian ♯1",                       1,2,1,2,2,1,3)

// whole tone

  f("whole tone",                                        2,2,2,2,2,2)

//****************************************************************************
}
//****************************************************************************
