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
  import utilities._                                     // For utilities

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
      assert(s == Scale(s.root,s.toSet)  ,               "[Note,Set[Note]]")
      assert(s == Scale(s.root,s.toSeq:_*),              "[Note,Seq[Note]]")
    }}
  }

  test("Scale invariants")
  {
    forAll("s") {(s: Scale) ⇒
    {
      assert(s.contains(s.root),                         "[root ∈ s]")
      assert(s.root  == s.note(0),                       "[root = s[0]]")
      assert(s.toSet == s.toSeq.toSet,                   "[toSet = toSeq]")
      assert(1<=s.size && s.size<=12,                    "[1 ≤ |s| ≤ 12]")
    }}

    forAll("s","n") {(s: Scale,n: Note) ⇒
    {
      assert(s.contains(n) iff s.toSet(n),               "[n ∈ s iff n ∈ toSet]")
      assert(s.indexOf(n).getOrElse(-1)==s.toSeq.indexOf(n),"[indexOf = toSeq.indexOf]")
    }}

    forAll("s","i") {(s: Scale,i: ℤ) ⇒
    {
      val n = mod(i,s.size)                              // The n'th note

      assert(s.note(i) == s.toSeq(n),                    "[s[] = toSeq[]]")
      assert(s.indexOf(s.note(i)) == Some(n),            "[indexOf∘note = id]")
    }}
  }

  test("Scale modes")
  {
    forAll("s","i") {(s: Scale,i: ℤ) ⇒
    {
      val M = s.modes                                    // Lists modes of s
      val m = s.mode(i)                                  // The i'th mode of s

      assert(M.contains(m),                              "[mode(i) ∈ modes]")
      assert(M.size  == s.size,                          "[s has |s| modes]")
      assert(m.toSet == s.toSet,                         "[modes share notes]")
      assert(m.root  == s.note(i),                       "[m.root = s[i]]")
    }}
  }

  test("mode is a ℤ-action")
  {
    implicit val i = Arbitrary(generate.int)             // For i ∈ [-128,128]
    implicit val t = new Transposing[Scale]
    {
      def apply(s: Scale,i: ℤ): Scale = s.mode(i)        // ...i'th mode of s
    }

    isAction[Scale,ℤ]()                                  // Verify the axioms
  }
}

//****************************************************************************
