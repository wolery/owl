//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Unit tests for class Shape.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//****************************************************************************

package com.wolery.owl.core;

//****************************************************************************

import org.scalacheck.Arbitrary
import org.scalacheck.Gen.choose
import org.scalatest.FunSuite

import CoreTest._
import CoreTest.arbitrary._

//****************************************************************************

class ShapeTest extends FunSuite
{
  test("Shape is transposing")
  {
    implicit val α = Arbitrary(choose(-128,128))

    isTransposing[Shape]()
  }

  test("Mode names are consistent")
  {
    def names(string: String): Unit =
    {
      val names   = string.split(":")
      val Some(s) = Shape(names(0))

      for (i ← 0 until names.size)
      {
        assert(Some(s + i) == Shape(names(i)),s"[${names(0)}]")
      }
    }

    names("chromatic:chromatic")
    names("whole half:half whole:whole half")
    names("ionian:dorian:phrygian:lydian:myxolydian:aeolian:locrian")
    names("melodic:dorian ♭2:lydian ♯5:lydian ♭7:mixolydian ♭6:dorian ♭5:altered")
    names("harmonic:locrian ♯6:ionian ♯5:dorian ♯4:phrygian ♯3:lydian ♯2:myxolydian ♯1")
    names("whole tone:whole tone")
  }

  test("Shape properties are consistent")
  {
    forAll("s") {(s: Shape) ⇒
    {
      assert(s == s)
      assert(s == Shape(s.intervals))
      assert(s == Shape(s.absolute:_*))
      assert(s == Shape(s.relative:_*))

      assert(s.absolute == s.intervals.toSeq)
      assert(s.absolute == (for (i ← 0 until s.size) yield s interval i))
      assert(s.modes    == (for (i ← 0 until s.size) yield s mode i))

      Shape(s.toString) map ((t: Shape) ⇒ assert(t == s))
    }}
  }
}

//****************************************************************************
