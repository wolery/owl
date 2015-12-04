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

class ShapeTest extends CoreSuite
{
  import arbitrary._                                     // For owl implicits

  test("Shape is transposing")
  {
    implicit val i = Arbitrary(generate.int)             // For i ∈ [-128,128]

    isTransposing[Shape]()                               // Verify the axioms
  }

  test("Shape construction")
  {
    forAll("s") {(s: Shape) ⇒
    {
      assert(s == Shape(s.toSet),                        "[Set[ℤ]]")
      assert(s == Shape(s.toSeq:_*),                     "[Seq[ℤ] (absolute)]")
      assert(s == Shape(s.absolute:_*),                  "[Seq[ℤ] (absolute)]")
      assert(s == Shape(s.relative:_*),                  "[Seq[ℤ] (relative)]")
    }}
  }

  test("Shape invariants")
  {
    forAll("s") {(s: Shape) ⇒
    {
      assert(s.contains(0),                              "[0 ∈ s]")
      assert(0 ∈ s.toSet,                                "[0 ∈ toSet]")
      assert(0 == s(0),                                  "[0 = s(0)]")
      assert(0 == s.toSeq(0),                            "[0 = toSeq(0)]")

      assert(s.size == s.toSet.size,                     "[size = toSet.size]")
      assert(s.size == s.toSeq.size,                     "[size = toSeq.size]")
      assert(s.toSet== s.toSeq.toSet,                    "[same notes]")
      assert(1<=s.size && s.size<=12,                    "[1 ≤ size ≤ 12]")

      assert(s.toSeq == (0 until s.size).map(s.apply(_)),"[check intervals]")
      assert(s.modes == (0 until s.size).map(s.mode(_)), "[check modes]")

      Shape(s.toString).map((t: Shape) ⇒ assert(t == s)) // Find it by name
    }}
  }

  test("Mode names are consistent")
  {
    /*
     * Verify that the names in the ':' delimited list each name a consecutive
     * mode of the first shape to be named in the list.
     *
     * @param	string A ':' delimited list of mode names.
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
