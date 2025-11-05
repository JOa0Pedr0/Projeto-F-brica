package controller;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import entities.Machine;
import service.MachineService;

@RestController
@RequestMapping("/api/maquinas")
public class MachineController {

	@Autowired
	private MachineService machineService;

	@GetMapping
	public List<Machine> listartodas() {
		return machineService.listarTodasMaquinas();
	}

}
