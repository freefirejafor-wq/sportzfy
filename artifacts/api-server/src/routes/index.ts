import { Router, type IRouter } from "express";
import healthRouter from "./health";
import sportsRouter from "./sports";
import proxyRouter from "./proxy";

const router: IRouter = Router();

router.use(healthRouter);
router.use(sportsRouter);
router.use(proxyRouter);

export default router;
