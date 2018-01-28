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
//*                                                                     0-0
//*                                                                   (| v |)
//**********************************************************************w*w***

package com.wolery
package owl
package core

//****************************************************************************

class ShapeTest extends CoreSuite
{
  import arbitrary._                                     // For Owl implicits
  import util.utilities.{mod,mod12}                      // For utilities

  test("Shape is a ℤ-set")
  {
    isℤSet[Shape]()                                      // Verify the axioms
  }

  test("Shape construction")
  {
    forAll("s") {(s: Shape) ⇒                            // ∀ s ∈ Shape
    {
      assert(s == Shape(s.toSet),                        "[Set[ℤ]]")
      assert(s == Shape(s.toSeq:_*),                     "[Seq[ℤ]]")
    }}
  }

  test("Shape invariants")
  {
    forAll("s") {(s: Shape) ⇒                            // ∀ s ∈ Shape
    {
      assert(s.contains(0),                              "[0 ∈ s]")
      assert(s.interval(0) == 0,                         "[s[0] = 0]")
      assert(s.toSet == s.toSeq.toSet,                   "[toSet = toSeq]")
      assert(s.size.isBetween(1,12),                     "[1 ≤ |s| ≤ 12]")
    }}

    forAll("s","i") {(s: Shape,i: ℤ) ⇒                   // ∀ s ∈ Shape, i ∈ ℤ
    {
      val n = mod12(i)                                   // Convert to interval

      assert(s.contains(n) iff s.toSet(n),               "[n ∈ s iff n ∈ toSet]")
      assert(s.indexOf(n).getOrElse(-1)==s.toSeq.indexOf(n),"[indexOf = toSeq.indexOf]")
    }}

    forAll("s","i") {(s: Shape,i: ℤ) ⇒                   // ∀ s ∈ Shape, i ∈ ℤ
    {
      val n = mod(i,s.size)                              // ...n'th interval

      assert(s.interval(n)            == s.toSeq(n),     "[s[] = toSeq[]]")
      assert(s.indexOf(s.interval(n)) == Some(n),        "[indexOf∘interval = id]")
    }}

    forAll("s","i") {(s: Shape,i: ℤ) ⇒                   // ∀ s ∈ Shape, i ∈ ℤ
    {
      assert(s.interval(i).isBetween(0,11),              "[0 ≤ s[i] ≤ 11]")
    }}
  }

  test("Mode names are consistent")
  {
    /**
     * Verify that the names in the ':' delimited list each name a consecutive
     * mode of the first shape to be named in the list.
     *
     * @param  string  A colon delimited list of mode names.
     */
    def modes(string: String): Unit =
    {
      val names   = string.split(":")                    // The list of names
      val Some(s) = Shape(names(0))                      // The first in list

      for ((n,i) ← names.zipWithIndex)                   // For each mode name
      {
        assert((s + i).name == Some(n),s"[mode $i of ${s.name} is named $n]")
      }
    }

    modes("chromatic:chromatic")
    modes("whole half:half whole:whole half")
    modes("ionian:dorian:phrygian:lydian:myxolydian:aeolian:locrian")
    modes("melodic:dorian ♭2:lydian ♯5:lydian ♭7:mixolydian ♭6:dorian ♭5:altered")
    modes("harmonic:locrian ♯6:ionian ♯5:dorian ♯4:phrygian ♯3:lydian ♯2:myxolydian ♯1")
    modes("whole tone:whole tone")
    modes("major pentatonic:suspended pentatonic:blues minor pentatonic:blues major pentatonic:minor pentatonic")
  }
}

//****************************************************************************
