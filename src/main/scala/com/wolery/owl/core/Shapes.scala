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

  def info(n: Name):  Maybe[Info]         = byNames.get(n.toLowerCase)
  def info(s: Shape): Maybe[Info]         = byShape.get(s)

//****************************************************************************

  private def f(names: String,intervals: ℤ*) =
  {
    assert(names.nonEmpty)

    val s = Shape(intervals: _*)
    val i = new Info(names,s)

    assert(!byShape.contains(s))

    byShape += s → i

    for (name ← names.split(":"))
    {
      byNames += name.toLowerCase → i
    }
  }

  private val byNames: Map[Name, Info] = Map.empty   // Indexed by name
  private val byShape: Map[Shape,Info] = Map.empty   // Indexed by shape

//****************************************************************************
// @see [[https://en.wikipedia.org/wiki/Jazz_scale Jazz scale (Wikipepdia)]]

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
  f("aeolian:minor:natural minor",                       2,1,2,2,1,2,2)
  f("locrian",                                           1,2,2,1,2,2,2)

// melodic minor

  f("melodic:melodic minor",                             2,1,2,2,2,2,1)
  f("dorian ♭2:phrygian ♮6)",                            1,2,2,2,2,1,2)
  f("lydian ♯5:lydian augmented:super lydian:acoustic",  2,2,2,2,1,2,1)
  f("lydian ♭7:lydian dominant:mixolydian ♯4",           2,2,2,1,2,1,2)
  f("mixolydian ♭6:melodic major",                       2,2,1,2,1,2,2)
  f("dorian ♭5:locrian ♮2:half diminished",              2,1,2,1,2,2,2)
  f("altered:altered dominant:super locrian",            1,2,1,2,2,2,2)

// harmonic minor
// @see [[http://docs.solfege.org/3.22/C/scales/har.html Harmonic minor]]

  f("harmonic:harmonic minor",                           2,1,2,2,1,3,1)
  f("locrian ♯6",                                        1,2,2,1,3,1,2)
  f("ionian ♯5:ionian augmented",                        2,2,1,3,1,2,1)
  f("dorian ♯4:romanian",                                2,1,3,1,2,1,2)
  f("phrygian ♯3:phrygian dominant",                     1,3,1,2,1,2,2)
  f("lydian ♯2",                                         3,1,2,1,2,2,1)
  f("myxolydian ♯1:ultra locrian",                       1,2,1,2,2,1,3)

// whole tone

  f("whole tone",                                        2,2,2,2,2,2)

// pentatonic
// @see [[https://en.wikipedia.org/wiki/Pentatonic_scale Pentatonic scale (Wikipedia)]]

  f("major pentatonic",                                  2,2,3,2,3)
  f("suspended pentatonic:egyptian",                     2,3,2,3,2)
  f("blues minor pentatonic:man gong",                   3,2,3,2,2)
  f("blues major pentatonic:ritusen",                    2,3,2,2,3)
  f("minor pentatonic",                                  3,2,2,3,2)

//****************************************************************************
}
//****************************************************************************
