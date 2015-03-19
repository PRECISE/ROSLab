import numpy as np
import random

sims = 1
min_dim = 3
max_dim = 8

for i in range(sims):
  result = []
  step = 1
  matrix = np.array([[1, 1, 0, 0, 0], [1, 0, 1, 1, 0], [0, 1, 1, 0, 1], [1, 0, 1, 1, 0], [0, 1, 0, 0, 1]])
  #matrix = np.random.randint(2, size=(random.randint(min_dim, max_dim), random.randint(min_dim, max_dim)))
  matrix = np.ma.array(matrix)
  # TODO: remove rows or columns that are completely zeros
  print "\n--Start--\n"
  while matrix.count():
    if len(matrix.nonzero()[0]) == 0:
      print "FAILED"
      break
    print "Step: " + str(step)
    print matrix
    sum_c = np.sum(matrix, axis=0)
    sum_c = np.ma.masked_equal(sum_c, 0) # mask out zeros
    sum_r = np.sum(matrix, axis=1)
    sum_r = np.ma.masked_equal(sum_r, 0) # mask out zeros
    print "\nSum_c"
    print sum_c
    print "\nSum_r"
    print sum_r
    for i in np.where(sum_r == sum_r.min())[0]:
      print "i: " + str(i)
      print "Matrix row"
      print matrix[i]
      minsum_c = np.ma.array(sum_c, mask = np.logical_not(matrix[i]))
      print "minsum_c"
      print minsum_c
      for j in np.where(minsum_c == minsum_c.min())[0]:
        print "j: " + str(j)
        result.append((i,j))
        matrix_mask = np.zeros(matrix.shape)
        matrix_mask[i] = 1
        matrix_mask[:,j] = 1
        matrix = np.ma.array(matrix, mask = matrix_mask)
        break
      break
    step += 1
  print "\n--Result--\n"
  print result