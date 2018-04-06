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
//*  See Also: http://tpolecat.github.io/2015/07/30/infer.html
//*                                                                     0-0
//*                                                                   (| v |)
//**********************************************************************w*w***

package com.wolery
package owl
package gui
package util

//****************************************************************************

import java.net.URL
import java.net.URLEncoder.encode

import javafx.fxml.FXMLLoader
import javafx.scene.layout.Pane
import javafx.scene.text.Font

import javax.sound.midi.{MidiSystem,Sequence,Soundbank}

//****************************************************************************

object load
{
  def view[Controller](name: String): (Pane,Controller) =
  {
    val l = new FXMLLoader(url(name,"fxml"))
    val n = l.load[Pane]
    val c = l.getController[Controller]

    (n,c)
  }

  def view[Controller](name: String,controller: Controller): (Pane,Controller) =
  {
    val l = new FXMLLoader(url(name,"fxml"))

    l.setController(controller)

    (l.load[Pane],controller)
  }

  def font(name: String,size: ℕ = 12): Font =
  {
    Font.loadFont(url(name,"ttf").toExternalForm,size);
  }

  def soundbank(name: String): Soundbank =
  {
    MidiSystem.getSoundbank(url(name,"sf2"))
  }

  def sequence(name: String): Sequence =
  {
    MidiSystem.getSequence(url(name,"mid"))
  }

  def theme(name: String): Option[String] =
  {
    val n = encode(name,"UTF-16").replace("+","%20")

    Option(s"/css/$n.css")
  }

  def url(path: String,kind: String): URL =
  {
    url(s"/$kind/$path.$kind")
  }

  def url(path: String): URL =
  {
    owl.getClass.getResource(path)
  }
}

//****************************************************************************
