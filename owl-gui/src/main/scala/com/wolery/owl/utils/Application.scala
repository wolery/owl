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

package com.wolery.owl.utils

import javafx.stage.Stage

//****************************************************************************

final class jfxapp extends javafx.application.Application
{
  override def init()             : Unit = Application.app.init()
  override def start(stage: Stage): Unit = Application.app.start(stage)
  override def stop()             : Unit = Application.app.stop()
}

object Application
{
  var app: Application = _
}

abstract class Application
{
  def main(args: Array[String]) =
  {
    Application.app = this
    m_args          = args
    javafx.application.Application.launch(classOf[jfxapp],args:_*)
  }

  def init()                          : Unit = {}
  def start(stage: Stage): Unit
  def stop()                          : Unit = {}

  def arguments = m_args

  private
  var m_args: Seq[String] = _
}

//****************************************************************************
