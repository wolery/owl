//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Unit tests for class Scale.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//****************************************************************************

package com.wolery.owl.core;

//****************************************************************************

class ScaleTest extends CoreSuite
{
  import arbitrary._                                     // For owl implicits
  import utilities.mod                                   // For mod(i,n)

  test("Scale is transposing")
  {
    implicit val i = Arbitrary(generate.int)             // For i ∈ [-128,128]

    isTransposing[Scale]()                               // Verify the axioms
  }

  test("Scale construction")
  {
    forAll("s") {(s: Scale) ⇒
    {
      assert(s == Scale(s.root,s.shape),                 "[Note,Shape]")
      assert(s == Scale(s.root,s.notes),                 "[Note,Notes]")
      assert(s == Scale(s.root,s.toSet)  ,               "[Note,Set[Note]]")
      assert(s == Scale(s.root,s.toSeq:_*),              "[Note,Seq[Note]]")
    }}
  }

  test("Scale invariants")
  {
    forAll("s") {(s: Scale) ⇒
    {
      assert(s.root ∈  s.notes,                          "[root ∈ notes]")
      assert(s.root ∈  s.toSet,                          "[root ∈ toSet]")
      assert(s.root == s.toSeq(0),                       "[root = toSeq(0)]")

      assert(s.size == s.notes.size,                     "[size = notes.size]")
      assert(s.size == s.toSet.size,                     "[size = toSet.size]")
      assert(s.size == s.toSeq.size,                     "[size = toSeq.size]")
      assert(1<=s.size && s.size<=12,                    "[1 ≤ size ≤ 12]")

      assert(s.notes == s.toSet,                         "[notes = toSet]")
      assert(s.notes == s.toSeq.toSet,                   "[notes = toSeq.toSet]")
    }}
  }

  test("Scale modes")
  {
    forAll("s","i") {(s: Scale,i: ℤ) ⇒
    {
      val M = s.modes                                    // all modes of s
      val m = s.mode(i)                                  // i'th mode of s

      assert(M.size == s.size,                           "[s has s.size modes]")
      assert(M.contains(m),                              "[mode(i) ∈ modes]")

      assert(m.toSeq(0) == s.toSeq(mod(i,s.size)),       "[m(0) = s(i)]")
      assert(m.notes == s.notes,                         "[same notes]")
    }}
  }

  test("mode is a ℤ-action")
  {
    implicit val i = Arbitrary(generate.int)             // For i ∈ [-128,128]
    implicit val t = new Transposing[Scale]
    {
      def apply(s: Scale,i: ℤ): Scale = s.mode(i)
    }

    isAction[Scale,ℤ]()                                  // Verify axioms
  }
}

//****************************************************************************
