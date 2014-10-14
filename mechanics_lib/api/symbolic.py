import math
from operator import add, mul, div, sub


class Symbol:
  def __init__(self, name, default=None):
    self.name = name
    self.default = default

  def __str__(self):
    return self.name

  def __repr__(self):
    if self.default is None:
      return 'SYMB[%s]' % self.name
    else:
      return 'SYMB[%s=%f]' % (self.name, self.default)

  def toLinearExpr(self):
    return LinearExpr({self: 1})

def testResult(result, op, *args):
  assert abs(LinearExpr.evalDefault(result) - op(*[LinearExpr.evalDefault(x) for x in args])) < 1e-6

def tested(op):
  def decorator(f):
    def actual(*args):
      result = f(*args)
      testResult(result, op, *args)
      return result
    return actual
  return decorator

class LinearExpr:
  def __init__(self, coeffs=None, const=0.0):
    if not coeffs: coeffs = {}
    self.coeffs = {}
    self.const = float(const)
    if abs(self.const) < 1e-7:
      self.const = 0.0
    for symbol in coeffs:
      if not abs(coeffs[symbol]) < 1e-7:
        self.coeffs[symbol] = coeffs[symbol]

  @tested(add)
  def __add__(self, other):
    if not isinstance(other, LinearExpr):
      return self + LinearExpr(const=other)
    newcoeffs = dict(self.coeffs)
    for var in other.coeffs:
      if var in newcoeffs:
        newcoeffs[var] += other.coeffs[var]
      else:
        newcoeffs[var] = other.coeffs[var]
    return LinearExpr(newcoeffs, self.const + other.const)

  __radd__ = __add__

  @tested(mul)
  def __mul__(self, other):
    if isinstance(other, LinearExpr):
      if len(self.coeffs) == 0:
        return other * self.const
      if len(other.coeffs) == 0:
        return self * other.const
      raise ("Multiplying two non-const linear expressions would result in a non-linear expression", self, other)
    else:
      scale = float(other)
      newcoeffs = {}
      for var in self.coeffs:
        newcoeffs[var] = self.coeffs[var] * scale
      return LinearExpr(newcoeffs, self.const * scale)

  __rmul__ = __mul__

  @tested(lambda x,y:float(x)/y)
  def __div__(self, other):
    if not isinstance(other, LinearExpr):
      return self * (1.0 / other)
    elif len(other.coeffs)==0:
      return self * (1.0 / other.const)
    else:
      ratio = [None]

      def feedNewRatio(newRatio):
        if ratio[0] is None:
          ratio[0] = newRatio
        elif abs(ratio[0] - newRatio) < 1e-6:
          pass
        else:
          raise ("Cannot divide due to non-constant quotient", self, other)

      for symbol in set(self.coeffs.keys() + other.coeffs.keys()):
        vala = self.coeffs[symbol] if symbol in self.coeffs else 0.0
        valb = other.coeffs[symbol] if symbol in other.coeffs else 0.0
        if valb == 0.0:
          raise ("Cannot divide due to non-constant quotient or division by zero", self, other)
        feedNewRatio(vala / valb)
      if ratio[0] is None:
        return self.const / other.const
      else:
        if not (abs(self.const) < 1e-6 and abs(self.const) < 1e-6):
          feedNewRatio(self.const / other.const)
      return ratio[0]

  @tested(lambda x,y:float(y)/x)
  def __rdiv__(self, other):
    if abs(other) < 1e-6 and self.coeffs != 0.0:
      return 0
    else:
      raise ("Cannot divide due to non-constant quotient or division by zero", self, other)

  @tested(sub)
  def __sub__(self, other):
    if not isinstance(other, LinearExpr):
      return self - LinearExpr(const=other)
    newcoeffs = dict(self.coeffs)
    for var in other.coeffs:
      if var in newcoeffs:
        newcoeffs[var] -= other.coeffs[var]
      else:
        newcoeffs[var] = -other.coeffs[var]
    return LinearExpr(newcoeffs, self.const - other.const)

  @tested(lambda x,y:y-x)
  def __rsub__(self, other):
    return LinearExpr(const=other) - self

  @tested(lambda x:-x)
  def __neg__(self):
    newcoeffs = {}
    for var in self.coeffs:
      newcoeffs[var] = -self.coeffs[var]
    return LinearExpr(newcoeffs, -self.const)

  @staticmethod
  @tested(lambda a,b:math.atan2(a,b))
  def atan2(a, b):
    if not isinstance(a, LinearExpr):
      a = LinearExpr(const=a)
    if not isinstance(b, LinearExpr):
      b = LinearExpr(const=b)
    defaultA = LinearExpr.evalDefault(a)
    defaultB = LinearExpr.evalDefault(b)

    res = [math.atan2(defaultA, defaultB)]  # enclosed in array to get around closure issues

    def feedNewRes(newRes):
      if abs(abs(newRes - res[0]) - math.pi) < 1e-6:
        pass
      elif abs(newRes - res[0]) < 1e-6:
        pass
      else:
        raise ("Cannot take atan2 of two linear expressions that do not have a constant ratio", a, b)

    for symbol in set(a.coeffs.keys() + b.coeffs.keys()):
      vala = a.coeffs[symbol] if symbol in a.coeffs else 0.0
      valb = b.coeffs[symbol] if symbol in b.coeffs else 0.0
      feedNewRes(math.atan2(vala, valb))
    if res is None:
      return math.atan2(a.const, b.const)
    else:
      if not (abs(a.const) < 1e-7 and abs(b.const) < 1e-7):
        feedNewRes(math.atan2(a.const, b.const))
    return res[0]

  @staticmethod
  @tested(lambda a,b:math.hypot(a,b))
  def hypot(a, b):
    if not isinstance(a, LinearExpr):
      a = LinearExpr(const=a)
    if not isinstance(b, LinearExpr):
      b = LinearExpr(const=b)

    LinearExpr.atan2(a, b)  # ensure that the ratio is constant
    aEval = LinearExpr.evalDefault(a)
    bEval = LinearExpr.evalDefault(b)
    result = math.hypot(aEval, bEval)
    if max(abs(aEval), abs(bEval)) < 1e-6:
      return LinearExpr(const=0.0)
    elif abs(aEval) < abs(bEval):
      return result / bEval * b
    else:
      return result / aEval * a


  def eval(self, assignment):
    newcoeffs = {}
    const = self.const
    for var in self.coeffs:
      if var in assignment:
        const += self.coeffs[var] * assignment[var]
      else:
        newcoeffs[var] = self.coeffs[var]
    if len(newcoeffs) > 0:
      return LinearExpr(newcoeffs, const)
    else:
      return const

  @staticmethod
  def evalDefault(expr):
    if not isinstance(expr, LinearExpr):
      return expr
    assignment = {}
    for symbol in expr.coeffs:
      if symbol.default is None:
        raise ("Cannot evaluate default, symbol missing default", expr)
      assignment[symbol] = symbol.default
    return expr.eval(assignment)

  def __str__(self):
    res = ''
    first = True
    if self.const != 0.0:
      res += str(self.const)
      first = False
    for symbol in self.coeffs:
      coeff = self.coeffs[symbol]
      if coeff > 0 and not first:
        res += '+' + str(coeff) + str(symbol.name)
      else:
        res += str(coeff) + str(symbol.name)
      first = False
    if res == '':
      return '0.0'
    return res

  def __eq__(self, other):
    if not isinstance(other, LinearExpr):
      return self.__eq__(LinearExpr(const=other))
    return (self.coeffs, self.const) == (other.coeffs, other.const)

  def __hash__(self):
    if len(self.coeffs) == 0:
      return hash(self.const)
    return hash((frozenset(self.coeffs.iteritems()), self.const))

  @staticmethod
  def round(expr, dig):
    if not isinstance(expr, LinearExpr):
      return LinearExpr.round(LinearExpr(const=expr), dig)
    else:
      newcoeffs = {}
      for var in expr.coeffs:
        newcoeffs[var] = round(expr.coeffs[var], dig)
      return LinearExpr(newcoeffs, round(expr.const, dig))

  __repr__ = __str__
