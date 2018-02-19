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

import gui._
import gui.util.implicits.asTask

//****************************************************************************

object owl extends Application
{
  val sequencer:   Sequencer   = MidiSystem.getSequencer()
  val synthesizer: Synthesizer = MidiSystem.getSynthesizer()

  def task: Task[Unit] =
  {
    synthesizer.open()
    sequencer.open()
    sequencer.getTransmitter.setReceiver(synthesizer.getReceiver)
  }

  def start(stage: Stage): Unit =
  {
    log.debug("start(stage)")

    SplashView(stage,task,MainView())
  }

  override
  def stop() =
  {
    sequencer.close()
    synthesizer.close()
  }
}

//****************************************************************************
