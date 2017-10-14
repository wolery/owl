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
//*
//*
//****************************************************************************

package com.wolery.owl.core;

//****************************************************************************

import utilities._
import Integer.bitCount

//****************************************************************************

class UtiltiesTest extends CoreSuite
{
  test("subscript")
  {
    assert(subscript("A(0123-+)Z") == "A₍₀₁₂₃₋₊₎Z")
    assert(subscript("A C") == "A C")
    assert(subscript("ABC") == "ABC")
  }

  test("superscript")
  {
    assert(superscript("A(0123-+)Z") == "A⁽⁰¹²³⁻⁺⁾Z")
    assert(superscript("A C") == "A C")
    assert(superscript("ABC") == "ABC")
  }

  test("isPowerOf2")
  {
    assert((-8 to +8).map(isPowerOf2(_)) == "FFFFFFFFFTTFTFFFT".map(_=='T'))
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