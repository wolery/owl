//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Unit tests for object utilities.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//****************************************************************************

package com.wolery.owl.core;

//****************************************************************************

import org.scalatest.FunSuite
import org.scalatest.prop.PropertyChecks

//****************************************************************************

class UtiltiesTest extends FunSuite with PropertyChecks
{
  import utilities._
  import Integer.bitCount

  test("mod")
  {
    forAll("i","n") {(i: ℤ,n: ℕ) ⇒ whenever (n > 0)
    {
      val r = mod(i,n)

      assert(0<=r && r<n)
      assert(0<=i implies i == r + n * (i/n))
    }}
  }

  test("mod12")
  {
    for (i ← 0 to  11) {assert(mod12(i) ==  i)}
    for (i ← 0 to -11) {assert(mod12(i) == -i)}

    forAll("i") {(i: ℤ) ⇒
    {
      val r = mod12(i)

      assert(0<=r && r<12)
      assert(0<=i implies i == r + 12 * (i/12))
    }}
  }

  test("rol12")
  {
    assert(rol12(0x001, 1) == 0x002,      "[1]")
    assert(rol12(0x001,23) == 0x800,      "[2]")
    assert(rol12(0x800, 1) == 0x001,      "[3]")
    assert(rol12(0x800,-4) == 0x080,      "[4]")

    forAll("bits","by") {(bits: ℤ,by: ℤ) ⇒
    {
      val b = clamp(Int.MinValue+12,Int.MaxValue-12)(by)
      val i = bits & 0xFFF
      val j = rol12(i,by)

      assert(0<=j && j<=0xFFF,            "[1]")
      assert(j == rol12(i,b + 12),        "[2]")
      assert(j == rol12(i,b - 12),        "[3]")
      assert(i == ror12(rol12(i,b),b),    "[4]")
      assert(bitCount(i) == bitCount(j),  "[5]")
    }}
  }

  test("ror12")
  {
    assert(ror12(0x001, 1) == 0x800,      "[1]")
    assert(ror12(0x001,25) == 0x800,      "[2]")
    assert(ror12(0x100, 1) == 0x080,      "[3]")
    assert(ror12(0x800,-4) == 0x008,      "[4]")

    forAll("bits","by") {(bits: ℤ,by: ℤ) ⇒
    {
      val b = clamp(Int.MinValue+12,Int.MaxValue-12)(by)
      val i = bits & 0xFFF
      val j = ror12(i,by)

      assert(0<=j && j<=0xFFF,            "[1]")
      assert(j == ror12(i,b + 12),        "[2]")
      assert(j == ror12(i,b - 12),        "[3]")
      assert(i == rol12(ror12(i,b),b),    "[4]")
      assert(bitCount(i) == bitCount(j),  "[5]")
    }}
  }

  def clamp(lo: ℤ,hi: ℤ)(i: ℤ) = i.min(hi).max(lo)
}

//****************************************************************************
