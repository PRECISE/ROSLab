# -*- coding: utf-8 -*-

# Form implementation generated from reading ui file 'quad.ui'
#
# Created: Tue Apr  8 17:18:58 2014
#      by: PyQt4 UI code generator 4.10.4
#
# WARNING! All changes made in this file will be lost!

from PyQt4 import QtCore, QtGui

try:
    _fromUtf8 = QtCore.QString.fromUtf8
except AttributeError:
    def _fromUtf8(s):
        return s

try:
    _encoding = QtGui.QApplication.UnicodeUTF8
    def _translate(context, text, disambig):
        return QtGui.QApplication.translate(context, text, disambig, _encoding)
except AttributeError:
    def _translate(context, text, disambig):
        return QtGui.QApplication.translate(context, text, disambig)

class Ui_MainWindow(object):
    def setupUi(self, MainWindow):
        MainWindow.setObjectName(_fromUtf8("MainWindow"))
        MainWindow.resize(1118, 475)
        self.centralwidget = QtGui.QWidget(MainWindow)
        self.centralwidget.setObjectName(_fromUtf8("centralwidget"))
        self.startButton = QtGui.QPushButton(self.centralwidget)
        self.startButton.setGeometry(QtCore.QRect(10, 10, 211, 101))
        font = QtGui.QFont()
        font.setPointSize(18)
        font.setBold(True)
        font.setWeight(75)
        self.startButton.setFont(font)
        self.startButton.setStyleSheet(_fromUtf8("QPushButton:checked {background: rgba(60, 179, 113, 255);}"))
        self.startButton.setCheckable(True)
        self.startButton.setObjectName(_fromUtf8("startButton"))
        self.nodeButton = QtGui.QPushButton(self.centralwidget)
        self.nodeButton.setEnabled(False)
        self.nodeButton.setGeometry(QtCore.QRect(10, 110, 211, 101))
        font = QtGui.QFont()
        font.setPointSize(18)
        font.setBold(True)
        font.setWeight(75)
        self.nodeButton.setFont(font)
        self.nodeButton.setStyleSheet(_fromUtf8("QPushButton:checked {background: rgba(60, 179, 113, 255);}"))
        self.nodeButton.setCheckable(True)
        self.nodeButton.setObjectName(_fromUtf8("nodeButton"))
        self.inputPlot = PlotWidget(self.centralwidget)
        self.inputPlot.setGeometry(QtCore.QRect(230, 10, 431, 391))
        self.inputPlot.setVerticalScrollBarPolicy(QtCore.Qt.ScrollBarAlwaysOff)
        self.inputPlot.setHorizontalScrollBarPolicy(QtCore.Qt.ScrollBarAlwaysOff)
        self.inputPlot.setObjectName(_fromUtf8("inputPlot"))
        self.rollPlot = PlotWidget(self.centralwidget)
        self.rollPlot.setGeometry(QtCore.QRect(670, 10, 211, 181))
        self.rollPlot.setVerticalScrollBarPolicy(QtCore.Qt.ScrollBarAlwaysOff)
        self.rollPlot.setHorizontalScrollBarPolicy(QtCore.Qt.ScrollBarAlwaysOff)
        self.rollPlot.setObjectName(_fromUtf8("rollPlot"))
        self.yawPlot = PlotWidget(self.centralwidget)
        self.yawPlot.setGeometry(QtCore.QRect(890, 220, 211, 181))
        self.yawPlot.setVerticalScrollBarPolicy(QtCore.Qt.ScrollBarAlwaysOff)
        self.yawPlot.setHorizontalScrollBarPolicy(QtCore.Qt.ScrollBarAlwaysOff)
        self.yawPlot.setObjectName(_fromUtf8("yawPlot"))
        self.pitchPlot = PlotWidget(self.centralwidget)
        self.pitchPlot.setGeometry(QtCore.QRect(890, 10, 211, 181))
        self.pitchPlot.setVerticalScrollBarPolicy(QtCore.Qt.ScrollBarAlwaysOff)
        self.pitchPlot.setHorizontalScrollBarPolicy(QtCore.Qt.ScrollBarAlwaysOff)
        self.pitchPlot.setObjectName(_fromUtf8("pitchPlot"))
        self.thrustLCD = QtGui.QLCDNumber(self.centralwidget)
        self.thrustLCD.setEnabled(False)
        self.thrustLCD.setGeometry(QtCore.QRect(680, 280, 181, 61))
        self.thrustLCD.setObjectName(_fromUtf8("thrustLCD"))
        self.thrustLabel = QtGui.QLabel(self.centralwidget)
        self.thrustLabel.setEnabled(False)
        self.thrustLabel.setGeometry(QtCore.QRect(680, 260, 181, 20))
        font = QtGui.QFont()
        font.setPointSize(14)
        self.thrustLabel.setFont(font)
        self.thrustLabel.setAlignment(QtCore.Qt.AlignCenter)
        self.thrustLabel.setObjectName(_fromUtf8("thrustLabel"))
        self.inputLabel = QtGui.QLabel(self.centralwidget)
        self.inputLabel.setGeometry(QtCore.QRect(420, 410, 56, 13))
        self.inputLabel.setAlignment(QtCore.Qt.AlignCenter)
        self.inputLabel.setObjectName(_fromUtf8("inputLabel"))
        self.rollLabel = QtGui.QLabel(self.centralwidget)
        self.rollLabel.setGeometry(QtCore.QRect(740, 200, 56, 13))
        self.rollLabel.setAlignment(QtCore.Qt.AlignCenter)
        self.rollLabel.setObjectName(_fromUtf8("rollLabel"))
        self.pitchLabel = QtGui.QLabel(self.centralwidget)
        self.pitchLabel.setGeometry(QtCore.QRect(970, 200, 56, 13))
        self.pitchLabel.setAlignment(QtCore.Qt.AlignCenter)
        self.pitchLabel.setObjectName(_fromUtf8("pitchLabel"))
        self.yawLabel = QtGui.QLabel(self.centralwidget)
        self.yawLabel.setGeometry(QtCore.QRect(970, 410, 56, 13))
        self.yawLabel.setAlignment(QtCore.Qt.AlignCenter)
        self.yawLabel.setObjectName(_fromUtf8("yawLabel"))
        MainWindow.setCentralWidget(self.centralwidget)
        self.menubar = QtGui.QMenuBar(MainWindow)
        self.menubar.setGeometry(QtCore.QRect(0, 0, 1118, 22))
        self.menubar.setObjectName(_fromUtf8("menubar"))
        MainWindow.setMenuBar(self.menubar)
        self.statusbar = QtGui.QStatusBar(MainWindow)
        self.statusbar.setObjectName(_fromUtf8("statusbar"))
        MainWindow.setStatusBar(self.statusbar)

        self.retranslateUi(MainWindow)
        QtCore.QMetaObject.connectSlotsByName(MainWindow)

    def retranslateUi(self, MainWindow):
        MainWindow.setWindowTitle(_translate("MainWindow", "MainWindow", None))
        self.startButton.setText(_translate("MainWindow", "Start", None))
        self.nodeButton.setText(_translate("MainWindow", "quad_joy", None))
        self.thrustLabel.setText(_translate("MainWindow", "Thrust", None))
        self.inputLabel.setText(_translate("MainWindow", "Input", None))
        self.rollLabel.setText(_translate("MainWindow", "Roll", None))
        self.pitchLabel.setText(_translate("MainWindow", "Pitch", None))
        self.yawLabel.setText(_translate("MainWindow", "Yaw", None))

from pyqtgraph import PlotWidget
