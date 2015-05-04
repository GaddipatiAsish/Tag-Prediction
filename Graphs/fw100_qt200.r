# Author : G Asish Kumar and Chinmaya Reddy*/
# Description: Plot to show Accuracy vs At least N no of tags predicted/suggested correctly

 x <- c(1, 2, 3, 4, 5)
 y <- c(86.9823270002321,31.93121238177128,4.905832952230491,0.40522875816993464,0.027820280984837947)
 plot(x, y, type='o', pch=1, col="blue", xlab="At least 'N' no of Tags Predicted Correctly", ylab="Accuracy(%)", ylim=c(0, 100), xlim=c(1,5))
 title(main="fwt = 100   qtt = 200", col.main="blue", font.main=4)