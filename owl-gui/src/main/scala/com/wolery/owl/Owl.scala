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

//****************************************************************************

import javafx.concurrent.Task

import javafx.stage.Stage

import javax.sound.midi.{MidiSystem,Sequencer,Synthesizer}

import com.wolery.fx.util.Application
import com.wolery.owl.gui.ConsoleView
import com.wolery.owl.gui.util.implicits.asTask
import com.wolery.owl.gui.PrototypeView
import com.wolery.owl.gui.splash

//****************************************************************************

object owl extends Application
{
  val sequencer:   Sequencer   = MidiSystem.getSequencer()
  val synthesizer: Synthesizer = MidiSystem.getSynthesizer()

  val initialize: Task[Unit] =
  {
    synthesizer.open()
    sequencer.open()
    sequencer.getTransmitter.setReceiver(synthesizer.getReceiver)
  }

  def start(stage: Stage): Unit =
  {
    //ConsoleView(stage)
    splash(stage,initialize,() ⇒ PrototypeView())
  }

  override
  def stop() =
  {
    sequencer.close()
    synthesizer.close()
  }
}

//****************************************************************************
