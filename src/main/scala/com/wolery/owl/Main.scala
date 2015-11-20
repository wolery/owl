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

package com.wolery.owl

import core._
import grizzled.slf4j._

object Main extends Logging
{
  def main(args: Array[String]): Unit =
  {
    info("Hello, world!")

    for (p ← C(-1) to G(9))
    {
      val m = p.midi
      val f = p.frequency
      val P = Pitch(p.frequency)

      println(f"$m%3s $p%4s $f%8s $P%4s")
    }
  }
}

//****************************************************************************
