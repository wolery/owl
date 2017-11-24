//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Unit tests for the core utilities.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*                                                                     0-0
//*                                                                   (| v |)
//**********************************************************************w*w***

package com.wolery.owl
package util

//****************************************************************************

import Integer.bitCount

//****************************************************************************

class UtiltiesTest extends OwlSuite
{
  test("subscript")
  {
    assert(subscript("A(0123-+)Z")   == "A₍₀₁₂₃₋₊₎Z",    "[1]")
    assert(subscript("A C")          == "A C",           "[2]")
    assert(subscript("ABC")          == "ABC",           "[3]")
  }

  test("superscript")
  {
    assert(superscript("A(0123-+)Z") == "A⁽⁰¹²³⁻⁺⁾Z",    "[1]")
    assert(superscript("A C")        == "A C",           "[2]")
    assert(superscript("ABC")        == "ABC",           "[3]")
  }

  test("isBetween")
  {
    assert((-8 to +8).map(_.isBetween(-2,+2)) ==
           "FFFFFFTTTTTFFFFFF".map(_ == 'T'),            "[1]")
  }

  test("isIncreasing")
  {
    assert( isIncreasing(0),                             "[1]")
    assert( isIncreasing(0,0),                           "[2]")
    assert( isIncreasing(0,1,2),                         "[3]")
    assert( isIncreasing(0,1,2,2),                       "[4]")
    reject( isIncreasing(1,0),                           "[5]")

    assert( isIncreasing(0.0),                           "[6]")
    assert( isIncreasing(0.0,0.0),                       "[7]")
    assert( isIncreasing(0.0,1.0,2.0),                   "[8]")
    assert( isIncreasing(0.0,1.0,2.0,2.0),               "[9]")
    reject( isIncreasing(1.0,0.0),                       "[10]")
  }

  test("isDecreasing")
  {
    assert( isDecreasing(0),                             "[1]")
    assert( isDecreasing(0,0),                           "[2]")
    assert( isDecreasing(2,1,1),                         "[3]")
    assert( isDecreasing(2,2,1,0),                       "[4]")
    reject( isDecreasing(0,1),                           "[5]")

    assert( isDecreasing(0.0),                           "[6]")
    assert( isDecreasing(0.0,0.0),                       "[7]")
    assert( isDecreasing(2.0,1.0,1.0),                   "[8]")
    assert( isDecreasing(2.0,2.0,1.0,0.0),               "[9]")
    reject( isDecreasing(0.0,1.0),                       "[10]")
  }

  test("isPowerOf2")
  {
    assert((-8 to +8).map(isPowerOf2) ==
           "FFFFFFFFFTTFTFFFT".map(_=='T'),              "[1]")
  }

  test("clamp")
  {
    assert(clamp(-1,-2,+1) == -1,                        "[1]")
    assert(clamp(-1,-1,+1) == -1,                        "[2]")
    assert(clamp(-1, 0,+1) ==  0,                        "[3]")
    assert(clamp(-1,+1,+1) == +1,                        "[4]")
    assert(clamp(-1,+2,+1) == +1,                        "[5]")
  }

  test("mod")
  {
    forAll("i","n") {(i: ℤ,n: ℕ) ⇒ whenever {n > 0}
    {
      val r = mod(i,n)

      assert(0<=r && r<n,                                "[0 ≤ r < n]")
      assert(0<=i implies i == n * (i/n) + r,            "[i = n⋅q + r]")
    }}
  }

  test("mod12")
  {
    for (i ← 0 to  11) {assert(mod12(i) ==  i,           "[i]" )}
    for (i ← 0 to -11) {assert(mod12(i) == -i,           "[-i]")}

    forAll("i") {(i: ℤ) ⇒
    {
      val r = mod12(i)

      assert(0<=r && r<12,                               "[0 ≤ r < 12]")
      assert(0<=i implies i == 12 * (i/12) + r,          "[i = 12⋅q + r]")
      assert(r == mod(i,12),                             "[mod12 = mod(_,12)]")
    }}
  }

  test("rol12")
  {
    assert(rol12(0x001, 1) == 0x002,                     "[1]")
    assert(rol12(0x001,23) == 0x800,                     "[2]")
    assert(rol12(0x800, 1) == 0x001,                     "[3]")
    assert(rol12(0x800,-4) == 0x080,                     "[4]")

    forAll("bits","by") {(bits: ℤ,by: ℤ) ⇒ whenever {notTooBig(by)}
    {
      val i = bits & 0xFFF
      val j = rol12(i,by)

      assert(0<=j && j<=0xFFF,                           "[1]")
      assert(j == rol12(i,by + 12),                      "[2]")
      assert(j == rol12(i,by - 12),                      "[3]")
      assert(i == ror12(rol12(i,by),by),                 "[4]")
      assert(bitCount(i) == bitCount(j),                 "[5]")
    }}
  }

  test("ror12")
  {
    assert(ror12(0x001, 1) == 0x800,                     "[1]")
    assert(ror12(0x001,25) == 0x800,                     "[2]")
    assert(ror12(0x100, 1) == 0x080,                     "[3]")
    assert(ror12(0x800,-4) == 0x008,                     "[4]")

    forAll("bits","by") {(bits: ℤ,by: ℤ) ⇒ whenever {notTooBig(by)}
    {
      val i = bits & 0xFFF
      val j = ror12(i,by)

      assert(0<=j && j<=0xFFF,                           "[1]")
      assert(j == ror12(i,by + 12),                      "[2]")
      assert(j == ror12(i,by - 12),                      "[3]")
      assert(i == rol12(ror12(i,by),by),                 "[4]")
      assert(bitCount(i) == bitCount(j),                 "[5]")
    }}
  }

  def notTooBig(i: ℤ) = Int.MinValue+12<=i && i<=Int.MaxValue-12
}

//****************************************************************************
