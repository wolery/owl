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
//*                                                                     0-0
//*                                                                   (| v |)
//**********************************************************************w*w***

package com.wolery
package owl
package gui

//****************************************************************************

import javax.sound.midi.Receiver
import javax.sound.midi.MidiMessage

//****************************************************************************

trait Controller extends Receiver
{
  def instrument: Instrument
  def view      : Pane

  def send(message: MidiMessage): Unit =
  {
    send(message,-1)
  }

  def close()   : Unit =
  {}
}

//****************************************************************************
