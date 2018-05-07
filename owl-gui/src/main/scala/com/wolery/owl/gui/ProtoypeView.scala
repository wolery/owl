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

import javax.sound.midi.{MetaEventListener, MetaMessage}

import javafx.event.ActionEvent
import javafx.scene.Scene
import javafx.scene.control.MenuBar
import javafx.stage.{Stage, StageStyle}

import com.wolery.owl.core._
import com.wolery.owl.gui.util.load
import com.wolery.owl.owl
import com.wolery.owl.transport.TransportController

import com.wolery.owl.midi.Transport

//****************************************************************************

class PrototypeController(controller: Controller,transport: MetaEventListener) extends Logging
{
  type Chord       = Seq[core.Pitch]
  type Pitches     = Seq[core.Pitch]
  @fx
  var menubar   : MenuBar    = _
  val instrument: Instrument = controller.instrument
  val playable  : Pitches    = instrument.playable

  def initialize() =
  {
    log.info("initialize")

    menubar.setUseSystemMenuBar(true)
    setup()
  }

  def onCIonian(ae: ActionEvent)    = {}
  def onCWholeTone(ae: ActionEvent) = {}
  def onClose()                     = {}
  def onPlay()                      =
  {
    owl.sequencer.setLoopStartPoint(0)
    owl.sequencer.setLoopStartPoint(1000)
  }

  def setup(): Unit =
  {
    owl.sequencer.getTransmitter.setReceiver(controller)
    owl.sequencer.addMetaEventListener(transport)
    owl.sequencer.addMetaEventListener(new MetaEventListener{def meta(m:MetaMessage):Unit = controller.send(m,-1)})
  }
}

//****************************************************************************

object PrototypeView
{
  def apply(): Unit =
  {
    val instrument = com.wolery.owl.gui.stringed.StringedInstrument(24,E(2),A(2),D(3),G(3),B(3),E(4))
    owl.sequencer.setSequence(load.sequence("time"))
    val transport = new Transport(owl.sequencer)

    val (_,c) = instrument.view("Guitar")
    val (t,x) = load.view("TransportView",new TransportController(transport))
    val (m,_) = load.view("PrototypeView",new PrototypeController(c,x))

    m.getChildren.addAll(c.view,t)

    new Stage(StageStyle.DECORATED)
    {
      setResizable(false)
      setTitle    ("Owl")
      setScene    (new Scene(m))
      show        ()
    }
  }
}

//****************************************************************************
