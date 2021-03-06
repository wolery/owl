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

package com.wolery
package owl
package util

//****************************************************************************

import Integer.bitCount
import com.wolery.owl.util.utilities._

//****************************************************************************

class UtiltiesTest extends test.Suite
{
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
