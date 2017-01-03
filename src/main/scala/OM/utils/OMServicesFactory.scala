package OM.utils

import OM.OMHttpService
import OM.services.OMServices
import com.greencatsoft.angularjs.{Factory, injectable}

/**
  * Created by jvelazquez on 1/3/2017.
  */
@injectable("omServices")
class OMServicesFactory(http: OMHttpService) extends Factory[OMServices] {
  override def apply() = new OMServices(http)
}
