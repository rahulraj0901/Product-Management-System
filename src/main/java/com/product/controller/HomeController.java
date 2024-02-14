package com.product.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.product.entity.Products;
import com.product.repository.ProductRepository;

@Controller
public class HomeController {

	@Autowired
	private ProductRepository productRepo;

	@GetMapping("/")
	public String home(Model m) {

		return findPaginateAndSorting(0, "id", "asc", m);
	}

	@GetMapping("/page/{pageNo}")
	public String findPaginateAndSorting(@PathVariable(value = "pageNo") int pageNo,
			@RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir, Model m) {

		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Pageable pageable = PageRequest.of(pageNo, 3, sort);

		Page<Products> page = productRepo.findAll(pageable);

		List<Products> list = page.getContent();

		m.addAttribute("pageNo", pageNo);
		m.addAttribute("totalElements", page.getTotalElements());
		m.addAttribute("totalPage", page.getTotalPages());
		m.addAttribute("all_products", list);

		m.addAttribute("sortField", sortField);
		m.addAttribute("sortDir", sortDir);
		m.addAttribute("revSortDir", sortDir.equals("asc") ? "desc" : "asc");

		return "index";
	}

	@GetMapping("/load_form")
	public String loadForm() {
		return "add";
	}

	@GetMapping("/edit_form/{id}")
	public String editForm(@PathVariable(value = "id") long id, Model m) {

		Optional<Products> product = productRepo.findById(id);

		Products pro = product.get();
		m.addAttribute("product", pro);

		return "edit";
	}

	@PostMapping("/save_products")
	public String saveProducts(@ModelAttribute Products products, HttpSession session) {

		productRepo.save(products);
		session.setAttribute("msg", "Product Added Sucessfully..");

		return "redirect:/load_form";
	}

	@PostMapping("/update_products")
	public String updateProducts(@ModelAttribute Products products, HttpSession session) {

		productRepo.save(products);
		session.setAttribute("msg", "Product Update Sucessfully..");

		return "redirect:/";
	}

	@GetMapping("/delete/{id}")
	public String deleteProducts(@PathVariable(value = "id") long id, HttpSession session) {
		productRepo.deleteById(id);
		session.setAttribute("msg", "Product Delete Sucessfully..");

		return "redirect:/";

	}

}
